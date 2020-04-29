import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives.{post, _}
import akka.http.scaladsl.server.{RequestContext, Route, RouteResult}
import StatusCodes._
import models.{ProductPost, UserPost}

import scala.concurrent.Future
import JsonSupport._
import factories.{ProductFactory, UserFactory}

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
        complete(ProductFactory.get)
      },
      post {
        entity(as[ProductPost]) {
          productPost => complete(ProductFactory.create(productPost).toString)
        }

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
