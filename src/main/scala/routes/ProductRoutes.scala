package routes

import akka.http.scaladsl.server.Directives.{as, complete, concat, entity, get, path, post}
import akka.http.scaladsl.server.Route
import stores.Store
import models.{ ProductPost, Product }
import models.JsonSupport._

class ProductRoutes(productStore: Store[Product]) {
  def getRoutes: Route = path("products") {
    concat(
      get {
        complete(productStore.get())
      },
      post {
        entity(as[ProductPost]) { productPost =>
          complete(productStore.create(Product.fromPost(productStore.nextId, productPost)).toString)
        }
      }
    )
  }
}
