import akka.http.scaladsl.testkit.ScalatestRouteTest
import models.{User, UserPost}
import JsonSupport._
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Route
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class UsersRouteSpec extends AnyWordSpecLike with Matchers with ScalatestRouteTest {
  val usersRoute: Route = Router.usersRouter
  val userPost: UserPost = UserPost("test", "test", "test@test.com")
  val userPostRequest: HttpRequest = Post("/users", userPost)

  "UsersRoute post" should {
    "return id of newly created user" in {
      userPostRequest ~> usersRoute ~> check {
        responseAs[String].shouldEqual("1")
      }
    }
    "return 409 if user with such email exists" in {
      userPostRequest ~> usersRoute
      userPostRequest ~> usersRoute ~> check {
        status.shouldEqual(Conflict)
      }
    }
  }

  "UsersRoute get" should {
    "return empty array by default" in {
      Get("/users") ~> usersRoute ~> check {
        responseAs[String].shouldEqual("[]")
      }
    }
    "return list of users" in {
      userPostRequest ~> usersRoute
      Get("/users") ~> usersRoute ~> check {
        responseAs[List[User]].shouldEqual(List(User.fromPost(1, userPost)))
      }
    }
  }
}
