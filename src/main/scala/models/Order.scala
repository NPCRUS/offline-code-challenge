package models

case class Order(
   email: String,
   deliveryAddress: String,
   items: List[CartItem]
)
