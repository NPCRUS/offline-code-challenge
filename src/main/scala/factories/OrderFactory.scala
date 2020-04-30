package factories

import models.{CartItem, Order, OrderPost}

object OrderFactory {
  private var orders: List[Order] = List.empty

  def get(email: String): List[Order] = orders.filter(_.email == email).reverse

  def create(email: String, orderPost: OrderPost, cartItems: List[CartItem]): Long = {
    val order = Order(nextId, email, orderPost.deliveryAddress, cartItems)
    orders = order :: orders
    order.id
  }

  private def nextId: Long = {
    orders.headOption match {
      case Some(order) => order.id + 1
      case None => 1
    }
  }
}
