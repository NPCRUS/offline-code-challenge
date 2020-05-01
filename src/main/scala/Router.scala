import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import models.{ User, Product }
import routes.{CartRoutes, OrderRoutes, ProductRoutes, UserRoutes}
import stores.Store

object Router {
  val userStore = new Store[User]
  val productStore = new Store[Product]

  def apply(): Route = {
    concat(
      new UserRoutes(userStore).getRoutes,
      new ProductRoutes(productStore).getRoutes,
      CartRoutes(),
      OrderRoutes()
    )
  }
}
