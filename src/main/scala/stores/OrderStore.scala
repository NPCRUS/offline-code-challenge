package stores

import models.{CartItem, Order, OrderPost}

object OrderStore extends IdSequence {
  private var orders: List[Order] = List.empty

  def get(email: String): List[Order] = orders.filter(_.email == email).reverse

  def create(email: String, orderPost: OrderPost, cartItems: List[CartItem]): Long = {
    val order = Order(nextId(orders), email, orderPost.deliveryAddress, cartItems)
    orders = order :: orders
    order.id
  }
}
