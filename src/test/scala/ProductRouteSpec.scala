import JsonSupport._
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import models.{ ProductPost, Product }
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class ProductRouteSpec extends AnyWordSpecLike with Matchers with ScalatestRouteTest {
  val productRoute: Route = Router.productsRouter
  val productPost: ProductPost = ProductPost(description = "coca-cola", price = 2, count = 100)
  val productPostRequest: HttpRequest = Post("/products", productPost)

  "ProductRoute post" should {
    "return id of newly created product" in {
      productPostRequest ~> productRoute ~> check {
        responseAs[String].shouldEqual("1")
      }
    }
  }

  "ProductRoute get" should {
    "return empty array by default" in {
      Get("/products") ~> productRoute ~> check {
        responseAs[List[Product]].shouldEqual(List.empty)
      }
    }
    "return list of users" in {
      productPostRequest ~> productRoute
      Get("/users") ~> productRoute ~> check {
        responseAs[List[Product]].shouldEqual(List(Product.fromPost(1, productPost)))
      }
    }
  }
}
