package routes

import akka.http.scaladsl.server.Directives.{as, complete, concat, entity, get, path, post}
import akka.http.scaladsl.server.Route
import stores.ProductStore
import models.ProductPost
import models.JsonSupport._

object ProductRoutes {
  def apply(): Route = path("products") {
    concat(
      get {
        complete(ProductStore.get)
      },
      post {
        entity(as[ProductPost]) {
          productPost => complete(ProductStore.create(productPost).toString)
        }

      }
    )
  }
}
