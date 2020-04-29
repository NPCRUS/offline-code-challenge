package factories

import models.{Product, ProductPost}

object ProductFactory {
  var products: List[Product] = List.empty

  def get: List[Product] = products.reverse

  def getById(id: Long): Option[Product] = products.find(_.id == id)

  def create(productPost: ProductPost): Long = {
    val product: Product = Product.fromPost(nextId, productPost)
    products = product :: products
    product.id
  }

  private def nextId: Long = {
    products.headOption match {
      case Some(product) => product.id + 1
      case None => 1
    }
  }
}
