import akka.http.javadsl.server.AuthorizationFailedRejection
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{HttpRequest, StatusCodes}
import akka.http.scaladsl.server.{MissingHeaderRejection, Route}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import stores.{CartStore, Store}
import models.JsonSupport._
import models.{CartItem, Order, OrderCreateError, OrderPost, Product, ProductPost, User, UserPost}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import routes.{CartRoutes, OrderRoutes}

class OrderRoutesSpec extends AnyWordSpecLike
  with Matchers
  with BeforeAndAfterEach
  with ScalatestRouteTest
{
  var cartStore: CartStore = new CartStore
  var productStore: Store[Product] = new Store[Product]
  var orderStore: Store[Order] = new Store[Order]
  var orderRoute: Route = new OrderRoutes(orderStore, cartStore, productStore, new Store[User]).getRoutes
  val product1: Product = Product(1, "coca-cola", 2, 100)
  val product2: Product = Product(2, "pepsi-cola", 1, 200)
  var user: User = User(1, "Nikita", "DE152332432324", "npcrus@gmail.com")
  val cartItem: CartItem = CartItem(1, 10)
  def authHeader: RawHeader = RawHeader("Authorization-Email", "npcrus@gmail.com")

  override def beforeEach(): Unit = {
    productStore = new Store[Product]
    val userStore = new Store[User]
    cartStore = new CartStore
    orderStore = new Store[Order]
    userStore.create(user)
    productStore.create(product1)
    productStore.create(product2)
    orderRoute = new OrderRoutes(orderStore, cartStore, productStore, userStore).getRoutes
  }

  "Order Route" should {
    "respond with missing Authorization-Email header if none presented" in {
      Get("/orders") ~> orderRoute ~> check {
        rejection.shouldEqual(MissingHeaderRejection("Authorization-Email"))
      }
    }
    "respond with unauthorized if provided email does not exist" in {
      Get("/orders") ~> RawHeader("Authorization-Email", "npcrus@gmail.com1") ~> orderRoute ~> check {
        rejection.isInstanceOf[AuthorizationFailedRejection]
      }
    }
  }

  "OrderRoute post" should {
    "respond with Not Found(empty cart) if cart id empty" in {
      Post("/orders", OrderPost("test")) ~> authHeader ~> orderRoute ~> check {
        status.shouldEqual(StatusCodes.NotFound)
      }
    }
    "respond with List(OrderCreateError) if amount ordered is more than in storage" in {
      cartStore.patch(user.email, cartItem)
      productStore.update(Product.withNewCount(product1, 0))
      Post("/orders", OrderPost("test")) ~> authHeader ~> orderRoute ~> check {
        status.shouldEqual(StatusCodes.Conflict)
        entityAs[List[OrderCreateError]].length.shouldEqual(1)
        entityAs[List[OrderCreateError]].head.productId.shouldEqual(cartItem.productId)
      }
    }
    "respond with 200 and clear cart and update product count" in {
      cartStore.patch(user.email, cartItem)
      Post("/orders", OrderPost("test")) ~> authHeader ~> orderRoute ~> check {
        status.shouldEqual(StatusCodes.OK)
        entityAs[String].shouldEqual("1")
      }
      Get("/orders") ~> authHeader ~> orderRoute ~> check {
        entityAs[List[Order]].shouldEqual(List(
          Order(1, user.email, "test", List(cartItem))
        ))
      }
      cartStore.get(user.email).shouldEqual(List.empty)
    }
  }

  "OrderRoute get" should {
    "return list of orders" in {
      cartStore.patch(user.email, cartItem)
      Post("/orders", OrderPost("test")) ~> authHeader ~> orderRoute
      Get("/orders") ~> authHeader ~> orderRoute ~> check {
        entityAs[List[Order]].shouldEqual(List(Order(1, user.email, "test", List(cartItem))))
      }
    }
    "return list of orders that belong to specific email" in {
      orderStore.create(Order(1, "test@test.com", "test", List(cartItem)))
      Get("/orders") ~> authHeader ~> orderRoute ~> check {
        entityAs[List[Order]].shouldEqual(List.empty)
      }
    }
  }
}
