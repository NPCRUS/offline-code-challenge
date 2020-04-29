package routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import factories.{CartFactory, ProductFactory}
import models.{CartItem, CartItemDelete, Product}
import models.JsonSupport._

object CartRoutes extends AuthorizeByEmail {
 def apply(): Route = path("cart") {
   headerValueByName("Authorization-Email") { email =>
     authorize(authorizeByEmail(email)) {
       concat(
         get {
           complete(CartFactory.get(email))
         },
         patch {
           entity(as[CartItem]) { cartItem =>
             if(!checkProductQuantityIntegrity(cartItem)) complete(StatusCodes.Conflict)
             else {
               CartFactory.add(email, cartItem)
               complete(StatusCodes.OK)
             }
           }
         },
         delete {
           entity(as[CartItemDelete]) { cartItemDelete =>
             CartFactory.remove(email, cartItemDelete.productId)
             complete(StatusCodes.OK)
           }
         }
       )
     }
   }
 }

  private def checkProductQuantityIntegrity(cartItem: CartItem): Boolean = {
    val productOption: Option[Product] = ProductFactory.getById(cartItem.productId)

    if(productOption.isEmpty) false // no such product
    else if(productOption.get.count < cartItem.quantity) false // not enough count of products
    else true
  }
}
