package routes

import akka.http.scaladsl.server.Directives.{as, complete, concat, entity, get, path, post}
import akka.http.scaladsl.server.Route
import factories.ProductFactory
import models.ProductPost
import models.JsonSupport._

object ProductRoutes {
  def apply(): Route = path("products") {
    concat(
      get {
        complete(ProductFactory.get)
      },
      post {
        entity(as[ProductPost]) {
          productPost => complete(ProductFactory.create(productPost).toString)
        }

      }
    )
  }
}
