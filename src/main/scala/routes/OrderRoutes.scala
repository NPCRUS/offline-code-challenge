package routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import stores.{CartStore, Store}
import models.JsonSupport._
import models.{CartItem, Order, OrderCreateError, OrderPost, User, Product}

class OrderRoutes(
    orderStore: Store[Order],
    cartStore: CartStore,
    productStore: Store[Product],
    userStore: Store[User]
) extends AuthorizeByEmail(userStore) {
  def getRoutes: Route = path("orders") {
   headerValueByName("Authorization-Email") { email =>
     authorize(authorizeByEmail(email)) {
       concat(
         get {
           complete(orderStore.get().filter(_.email == email))
         },
         post {
           entity(as[OrderPost])(createOrder(email, _))
         }
       )
     }
   }
  }

  private def createOrder(email: String, orderPost: OrderPost): Route = {
    // this code suppose to use some sort of lock mechanism or something to avoid race conditions(simultaneous order creations)
    // this code suppose to use some sort of transaction mechanism in order to not have any dangling state
    val cart = cartStore.get(email)
    val cartErrors = getCartErrors(cart)

    if(cart.isEmpty) complete(StatusCodes.NotFound, "cart is empty")
    else if(cartErrors.nonEmpty) complete(StatusCodes.Conflict, cartErrors)
    else {
      // update quantities on products
      cart.foreach(ci => {
        productStore.getById(ci.productId) match {
          case Some(p) => {
            val newProduct =  Product(p.id, p.description, p.price, p.count - ci.quantity)
            productStore.update(newProduct)
          }
          case None => // shouldn't happen
        }
      })
      // clear cart
      cartStore.clear(email)
      // create order
      val newOrder = Order(orderStore.nextId, email, orderPost.deliveryAddress, cart)
      complete(orderStore.create(newOrder).toString)
    }
  }

  private def getCartErrors(cart: List[CartItem]): List[OrderCreateError] = {
    cart.flatMap(ci => {
      productStore.getById(ci.productId) match {
        case Some(p) =>
          if(p.count < ci.quantity) List(OrderCreateError(ci.productId, s"max amount of items ${p.count}"))
          else List.empty
        case None => List(OrderCreateError(ci.productId, "product with such id does not exist"))
      }
    })
  }
}
