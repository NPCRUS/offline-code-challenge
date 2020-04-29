package models

case class CartItem(
  productId: Long,
  quantity: Int
)

case class CartItemDelete(
  productId: Long
)
