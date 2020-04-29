import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives.{post, _}
import akka.http.scaladsl.server.{RequestContext, Route, RouteResult}
import StatusCodes._
import models.UserPost

import scala.concurrent.Future
import JsonSupport._
import factories.UserFactory

object Router {
  def apply(): RequestContext => Future[RouteResult] = {
    concat(
      ordersRouter,
      cartRouter,
      productsRouter,
      usersRouter
    )
  }

  def ordersRouter: Route = path("orders") {
    post {
      complete(HttpEntity("create order"))
    }
  }

  def cartRouter: Route = path("cart") {
    concat(
      get {
        complete("cart content")
      },
      post {
        complete("add to cart")
      },
      delete {
        complete("delete from cart")
      }
    )
  }

  def productsRouter: Route = path("products") {
    concat(
      get {
        complete(HttpEntity("list of products"))
      },
      post {
        complete(HttpEntity("create product"))
      }
    )
  }

  def usersRouter: Route = path("users") {
    concat(
      get {
        complete(UserFactory.get)
      },
      post {
        entity(as[UserPost]) {
          userPost => complete {
            UserFactory.create(userPost) match {
              case Some(id) => id.toString
              case None => Conflict
            }
          }
        }
      }
    )
  }
}
