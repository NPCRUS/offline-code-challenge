package models

object Product {
  def fromPost(id: Long, post: ProductPost): Product =
    Product(id, post.description, post.price, post.count)
}

case class Product(
  override val id: Long,
  description: String,
  price: Int,
  count: Int
) extends Identifier(id)

case class ProductPost(
  description: String,
  price: Int,
  count: Int
)
