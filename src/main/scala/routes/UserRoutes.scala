package routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{as, complete, concat, entity, get, path, post}
import akka.http.scaladsl.server.Route
import stores.Store
import models.{User, UserPost}
import models.JsonSupport._

object UserRoutes {
  def apply(userStore: Store[User]): Route = path("users") {
    concat(
      get {
        complete(userStore.get())
      },
      post {
        entity(as[UserPost])(userPost =>  createUser(userStore, userPost))
      }
    )
  }

  def createUser(userStore: Store[User], userPost: UserPost): Route = {
    userStore.get().find(_.email == userPost.email) match {
      case Some(_) => complete(StatusCodes.Conflict)
      case None =>
        val user: User = User.fromPost(userStore.nextId, userPost)
        userStore.create(user)
        complete(user.id.toString)
    }
  }
}
