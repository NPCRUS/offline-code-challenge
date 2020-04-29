package factories

import models.{Product, ProductPost}

object ProductFactory {
  var products: List[Product] = List.empty

  def get: List[Product] = products

  def getById(id: Long): Option[Product] = products.find(_.id == id)

  def create(productPost: ProductPost): Long = {
    def inc: Long = {
      products.lastOption match {
        case Some(product) => product.id + 1
        case None => 1
      }
    }

    val product: Product = Product.fromPost(inc, productPost)
    products = products :+ product
    product.id
  }
}
