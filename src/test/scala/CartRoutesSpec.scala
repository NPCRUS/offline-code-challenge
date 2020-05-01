import akka.http.javadsl.server.AuthorizationFailedRejection
import akka.http.scaladsl.model.headers.RawHeader
import models.JsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{MissingHeaderRejection, Route}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import stores.{CartStore, Store}
import models.{CartItem, CartItemDelete, Product, ProductPost, User, UserPost}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import routes.CartRoutes

class CartRoutesSpec extends AnyWordSpecLike
  with Matchers
  with BeforeAndAfterEach
  with ScalatestRouteTest {
  var cartRoute: Route = new CartRoutes(new CartStore(), new Store[Product], new Store[User]).getRoutes
  val product1: Product = Product(1, "coca-cola", 2, 100)
  val product2: Product = Product(2, "pepsi-cola", 1, 200)
  var user: User = User(1, "Nikita", "DE152332432324", "npcrus@gmail.com")
  def authHeader: RawHeader = RawHeader("Authorization-Email", "npcrus@gmail.com")

  override def beforeEach(): Unit = {
    val productStore = new Store[Product]
    val userStore = new Store[User]
    userStore.create(user)
    productStore.create(product1)
    productStore.create(product2)
    cartRoute = new CartRoutes(new CartStore, productStore, userStore).getRoutes
  }

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
    "respond with 409 if product does not exist" in {
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
    "updates amount in cart to new amount" in {
      Patch("/cart", CartItem(1, 50)) ~> authHeader ~> cartRoute
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
