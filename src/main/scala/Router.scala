import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives.{post, _}
import akka.http.scaladsl.server.{RequestContext, RouteResult}

import scala.concurrent.Future

object Router {
  def apply(): RequestContext => Future[RouteResult] = {
    concat(
      // products related
      path("products") {
        concat(
          get {
            complete(HttpEntity("list of products"))
          },
          post {
            complete(HttpEntity("create product"))
          }
        )
      },

      // cart related
      path("cart") {
        get {
          complete(HttpEntity("get items in cart"))
        }
      },
      path("cart/add") {
        post {
          complete(HttpEntity("add item into cart"))
        }
      },
      path("cart/remove") {
        post {
          complete(HttpEntity("remove item from cart"))
        }
      },

      // users
      path("users") {
        post {
          complete(HttpEntity("create user"))
        }
      },

      // orders
      path("orders") {
        post {
          complete(HttpEntity("create order"))
        }
      }
    )
  }
}
