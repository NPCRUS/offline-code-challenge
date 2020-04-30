package routes

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import stores.{CartStore, OrderStore, ProductStore}
import models.JsonSupport._
import models.{CartItem, CartItemDelete, OrderCreateError, OrderPost, Product}

object OrderRoutes extends AuthorizeByEmail {
  def apply(): Route = path("orders") {
   headerValueByName("Authorization-Email") { email =>
     authorize(authorizeByEmail(email)) {
       concat(
         get {
           complete(OrderStore.get(email))
         },
         post {
           entity(as[OrderPost]) { orderPost =>
             val cart = CartStore.get(email)
             val cartErrors = CartIntegrity.getCartErrors(cart)

             if(cart.isEmpty) complete(StatusCodes.NotFound, "cart is empty")
             else if(cartErrors.nonEmpty) complete(StatusCodes.Conflict, cartErrors)
             else complete(createOrder(email, orderPost, cart).toString)
           }
         }
       )
     }
   }
  }

  private def createOrder(email: String, orderPost: OrderPost, cart: List[CartItem]): Long = {
    // this code suppose to use some sort of lock mechanism or something to avoid race conditions(simultaneous order creations)
    // this code suppose to use some sort of transaction mechanism in order to not have any dangling state

    // update quantities on products
    cart.foreach(ci => {
      ProductStore.getById(ci.productId) match {
        case Some(p) => ProductStore.update(ci.productId, p.count - ci.quantity)
        case None => // shouldn't happen
      }
    })
    // clear cart
    CartStore.clear(email)
    // create order
    OrderStore.create(email, orderPost, cart)

  }

  object CartIntegrity {
    def getCartErrors(cart: List[CartItem]): List[OrderCreateError] = {
      cart.flatMap(ci => {
        ProductStore.getById(ci.productId) match {
          case Some(p) =>
            if(p.count < ci.quantity) List(OrderCreateError(ci.productId, s"max amount of items ${p.count}"))
            else List.empty
          case None => List(OrderCreateError(ci.productId, "product with such id does not exist"))
        }
      })
    }
  }
}
