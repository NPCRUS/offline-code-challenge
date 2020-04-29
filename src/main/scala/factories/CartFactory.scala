package factories

import models.CartItem

object CartFactory {
  var carts: Map[String, List[CartItem]] = Map.empty

  def get(key: String): List[CartItem] = carts.getOrElse(key, List.empty)

  def add(key: String, item: CartItem): Unit = {
    def updateOrAdd(cart: List[CartItem], item: CartItem): List[CartItem] = {
      if(cart.exists(_.productId == item.productId)) {
        cart.map(ci => {
          if(ci.productId == item.productId) CartItem(item.productId, item.quantity)
          else ci
        })
      } else item :: cart
    }

    val cart = carts.getOrElse(key, List.empty)

    carts = carts.updated(key, updateOrAdd(cart, item))
  }

  def remove(key: String, productId: Long): Unit = {
    val cart = carts.getOrElse(key, List.empty)

    carts = carts.updated(key, cart.filter(_.productId != productId))
  }
}
