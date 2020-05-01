import akka.http.scaladsl.testkit.ScalatestRouteTest
import models.{User, UserPost}
import models.JsonSupport._
import routes.UserRoutes
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Route
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import stores.Store

class UserRoutesSpec extends AnyWordSpecLike
  with Matchers
  with BeforeAndAfterEach
  with  ScalatestRouteTest
{
  var usersRoute: Route = new UserRoutes(new Store[User]).getRoutes
  val userPost: UserPost = UserPost(name = "test", bankAccount = "test", email = "test@test.com")
  val userPostRequest: HttpRequest = Post("/users", userPost)

  override def beforeEach() {
    usersRoute = new UserRoutes(new Store[User]).getRoutes
  }

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
    "return list of users" in {
      userPostRequest ~> usersRoute
      Get("/users") ~> usersRoute ~> check {
        responseAs[List[User]].shouldEqual(List(User.fromPost(1, userPost)))
      }
    }
  }
}
