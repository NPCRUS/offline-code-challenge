import models.JsonSupport._
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import models.{Product, ProductPost}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import routes.ProductRoutes
import stores.Store

class ProductRoutesSpec extends AnyWordSpecLike
  with Matchers
  with BeforeAndAfterEach
  with ScalatestRouteTest
{
  var productRoute: Route = new ProductRoutes(new Store[Product]).getRoutes
  val productPost: ProductPost = ProductPost(description = "coca-cola", price = 2, count = 100)
  val productPostRequest: HttpRequest = Post("/products", productPost)

  override def beforeEach(): Unit = {
    productRoute = new ProductRoutes(new Store[Product]).getRoutes
  }

  "ProductRoute post" should {
    "return id of newly created product" in {
      productPostRequest ~> productRoute ~> check {
        responseAs[String].shouldEqual("1")
      }
    }
  }

  "ProductRoute get" should {
    "return list of products" in {
      productPostRequest ~> productRoute
      Get("/products") ~> productRoute ~> check {
        responseAs[List[Product]].shouldEqual(List(Product.fromPost(1, productPost)))
      }
    }
  }
}
