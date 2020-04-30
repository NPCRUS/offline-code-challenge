import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import routes.{CartRoutes, OrderRoutes, ProductRoutes, UserRoutes}

object Router {
  def apply(): Route = {
    concat(
      UserRoutes(),
      ProductRoutes(),
      CartRoutes(),
      OrderRoutes()
    )
  }
}
