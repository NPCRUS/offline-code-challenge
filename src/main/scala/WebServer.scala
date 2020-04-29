import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import factories.{ProductFactory, UserFactory}
import models.{ProductPost, UserPost}

import scala.concurrent.ExecutionContext
import scala.io.StdIn

object WebServer extends App {
  implicit val system: ActorSystem = ActorSystem("web-server")
  implicit val executionContext: ExecutionContext = system.dispatcher

  // some seeds
  ProductFactory.create(ProductPost("coca-cola", 2, 100))
  ProductFactory.create(ProductPost("pepsi-cola", 1, 200))
  UserFactory.create(UserPost("Nikita", "DE152332432324", "npcrus@gmail.com"))

  val bindingFuture = Http().bindAndHandle(Router(), "localhost", 8080)

  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
