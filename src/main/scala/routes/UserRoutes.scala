package routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{as, complete, concat, entity, get, path, post}
import akka.http.scaladsl.server.Route
import factories.UserFactory
import models.UserPost
import models.JsonSupport._

object UserRoutes {
  def apply(): Route = path("users") {
    concat(
      get {
        complete(UserFactory.get)
      },
      post {
        entity(as[UserPost]) {
          userPost => complete {
            UserFactory.create(userPost) match {
              case Some(id) => id.toString
              case None => StatusCodes.Conflict
            }
          }
        }
      }
    )
  }
}
