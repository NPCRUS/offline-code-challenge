import akka.http.javadsl.server.AuthorizationFailedRejection
import akka.http.scaladsl.model.headers.RawHeader
import models.JsonSupport._
import akka.http.scaladsl.model.{HttpHeader, HttpRequest, StatusCodes}
import akka.http.scaladsl.server.{MissingHeaderRejection, Route}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import factories.{ProductFactory, UserFactory}
import models.{CartItem, CartItemDelete, Product, ProductPost, UserPost}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import routes.CartRoutes

class CartRoutesSpec extends AnyWordSpecLike with Matchers with ScalatestRouteTest {
  val cartRoute: Route = CartRoutes()
  // some seeds
  ProductFactory.create(ProductPost("coca-cola", 2, 100))
  ProductFactory.create(ProductPost("pepsi-cola", 1, 200))
  UserFactory.create(UserPost("Nikita", "DE152332432324", "npcrus@gmail.com"))
  def authHeader: RawHeader = RawHeader("Authorization-Email", "npcrus@gmail.com")

  val productPost: ProductPost = ProductPost(description = "coca-cola", price = 2, count = 100)
  val productPostRequest: HttpRequest = Post("/products", productPost)

  "Cart Route" should {
    "respond with missing Authorization-Email header if none presented" in {
      Get("/cart") ~> cartRoute ~> check {
        rejection.shouldEqual(MissingHeaderRejection("Authorization-Email"))
      }
    }
    "respond with unauthorized if provided email does not exist" in {
      Get("/cart") ~> RawHeader("Authorization-Email", "npcrus@gmail.com1") ~> cartRoute ~> check {
        rejection.isInstanceOf[AuthorizationFailedRejection]
      }
    }
  }

  "CartRoute patch" should {
    "respond with 409 if product is not presented" in {
      Patch("/cart", CartItem(3, 100)) ~> authHeader ~> cartRoute ~> check {
        status.shouldEqual(StatusCodes.Conflict)
      }
    }
    "respond with 409 if quantity of product is less then requested amount" in {
      Patch("/cart", CartItem(2, 500)) ~> authHeader ~> cartRoute ~> check {
        status.shouldEqual(StatusCodes.Conflict)
      }
    }
    "respond with 200 and successfully puts item into cart" in {
      Patch("/cart", CartItem(1, 100)) ~> authHeader ~> cartRoute ~> check {
        status.shouldEqual(StatusCodes.OK)
        Get("/cart") ~> authHeader ~> cartRoute ~> check {
          responseAs[List[CartItem]].shouldEqual(List(CartItem(1, 100)))
        }
      }
    }
  }

  "Cart route delete" should {
    "respond with OK even if there was nothing to delete in cart" in {
      Delete("/cart", CartItemDelete(5)) ~> authHeader ~> cartRoute ~> check {
        status.shouldEqual(StatusCodes.OK)
      }
    }
    "respond with OK and successfully delete item from cart" in {
      Patch("/cart", CartItem(1, 100)) ~> authHeader ~> cartRoute
      Delete("/cart", CartItemDelete(1)) ~> authHeader ~> cartRoute ~> check {
        status.shouldEqual(StatusCodes.OK)
        Get("/cart") ~> authHeader ~> cartRoute ~> check {
          responseAs[List[CartItem]].shouldEqual(List.empty)
        }
      }
    }
  }

  "CartRoute get" should {
    "respond with cart items" in {
      Patch("/cart", CartItem(1, 100)) ~> authHeader ~> cartRoute
      Get("/cart") ~> authHeader ~> cartRoute ~> check {
        responseAs[List[CartItem]].shouldEqual(List(CartItem(1, 100)))
      }
    }
  }
}
