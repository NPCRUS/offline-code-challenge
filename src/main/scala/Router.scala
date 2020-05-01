import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import models.User
import routes.{CartRoutes, OrderRoutes, ProductRoutes, UserRoutes}
import stores.Store

object Router {
  val userStore = new Store[User]

  def apply(): Route = {
    concat(
      UserRoutes(userStore),
      ProductRoutes(),
      CartRoutes(),
      OrderRoutes()
    )
  }
}
