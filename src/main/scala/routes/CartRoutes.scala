package routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import stores.{CartStore, Store}
import models.{CartItem, CartItemDelete, Product, User}
import models.JsonSupport._

class CartRoutes(
   cartStore: CartStore,
   productStore: Store[Product],
   userStore: Store[User]
) extends AuthorizeByEmail(userStore) {
  def getRoutes: Route = path("cart") {
   headerValueByName("Authorization-Email") { email =>
     authorize(authorizeByEmail(email)) {
       concat(
         get {
           complete(cartStore.get(email))
         },
         patch {
           entity(as[CartItem])(patchCart(email, _))
         },
         delete {
           entity(as[CartItemDelete])(deleteCart(email, _))
         }
       )
     }
   }
  }

  def patchCart(email: String, cartItem: CartItem): Route = {
    val productOption: Option[Product] = productStore.getById(cartItem.productId)

    if(productOption.isEmpty) complete(StatusCodes.Conflict, "no such product")
    else if(productOption.get.count < cartItem.quantity) complete(StatusCodes.Conflict, "not enough items")
    else {
      cartStore.patch(email, cartItem)
      complete(StatusCodes.OK)
    }
  }

  def deleteCart(email: String, body: CartItemDelete): Route = {
    cartStore.remove(email, body.productId)
    complete(StatusCodes.OK)
  }
}
