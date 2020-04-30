package routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{as, complete, concat, entity, get, path, post}
import akka.http.scaladsl.server.Route
import stores.UserStore
import models.UserPost
import models.JsonSupport._

object UserRoutes {
  def apply(): Route = path("users") {
    concat(
      get {
        complete(UserStore.get)
      },
      post {
        entity(as[UserPost]) {
          userPost => complete {
            UserStore.create(userPost) match {
              case Some(id) => id.toString
              case None => StatusCodes.Conflict
            }
          }
        }
      }
    )
  }
}
