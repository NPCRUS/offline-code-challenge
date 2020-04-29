import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import factories.UserFactory
import models.UserPost

import scala.concurrent.ExecutionContext
import scala.io.StdIn

object WebServer extends App {
  implicit val system: ActorSystem = ActorSystem("web-server")
  implicit val executionContext: ExecutionContext = system.dispatcher

  import JsonSupport._
  val route = path("users") {
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

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
