package models

case class Order(
  id: Long,
  email: String,
  deliveryAddress: String,
  items: List[CartItem]
) extends Entity

case class OrderPost(
  deliveryAddress: String
)

case class OrderCreateError(
  productId: Long,
  message: String
)
