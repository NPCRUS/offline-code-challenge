import akka.http.scaladsl.testkit.ScalatestRouteTest
import models.UserPost
import JsonSupport._
import akka.http.scaladsl.server.Route
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class UsersRouteSpec extends AnyWordSpecLike with Matchers with ScalatestRouteTest {
  val usersRoute: Route = Router.usersRouter

  "UsersRoute post" should {
    "return id of newly created user" in {
      Post("/users", UserPost("test", "test", "test")) ~> usersRoute ~> check {
        responseAs[String].shouldEqual("1")
      }
    }
  }
}
