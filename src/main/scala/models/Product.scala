package models

object Product {
  def fromPost(id: Long, post: ProductPost): Product =
    Product(id = id, description = post.description, price = post.price, count = post.count)
}

case class Product(
  id: Long,
  description: String,
  price: Int,
  count: Int
) extends Entity

case class ProductPost(
  description: String,
  price: Int,
  count: Int
)
