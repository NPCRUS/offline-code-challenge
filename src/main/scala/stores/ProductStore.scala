package stores

import models.{Product, ProductPost}

object ProductStore extends IdSequence {
  private var products: List[Product] = List.empty

  def get: List[Product] = products.reverse

  def getById(id: Long): Option[Product] = products.find(_.id == id)

  def create(productPost: ProductPost): Long = {
    val product: Product = Product.fromPost(nextId(products), productPost)
    products = product :: products
    product.id
  }

  def update(productId: Long, count: Int): Unit = {
    products = products.map(p => {
      if(p.id == productId) Product(p.id, p.description, p.price, count)
      else p
    })
  }
}
