package models

object Product {
  def fromPost(id: Long, post: ProductPost): Product =
    Product(id, post.description, post.price, post.count)
}

case class Product(
  id: Long,
  description: String,
  price: Int,
  count: Int
)

case class ProductPost(
  description: String,
  price: Int,
  count: Int
)