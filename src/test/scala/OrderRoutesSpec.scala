import akka.http.javadsl.server.AuthorizationFailedRejection
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{HttpRequest, StatusCodes}
import akka.http.scaladsl.server.{MissingHeaderRejection, Route}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import factories.{CartFactory, OrderFactory, ProductFactory, UserFactory}
import models.JsonSupport._
import models.{CartItem, CartItemDelete, Order, OrderCreateError, OrderPost, ProductPost, UserPost}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import routes.{CartRoutes, OrderRoutes}

class OrderRoutesSpec extends AnyWordSpecLike with Matchers with ScalatestRouteTest {
  val orderRoute: Route = OrderRoutes()
  val cartRoute: Route = CartRoutes()
  // some seeds
  ProductFactory.create(ProductPost("coca-cola", 2, 100))
  ProductFactory.create(ProductPost("pepsi-cola", 1, 200))
  UserFactory.create(UserPost("Nikita", "DE152332432324", "npcrus@gmail.com"))
  def authHeader: RawHeader = RawHeader("Authorization-Email", "npcrus@gmail.com")

  val cartItem: CartItem = CartItem(1, 10)
  val cartAddPatch: HttpRequest = Patch("/cart", cartItem)

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
        entityAs[String].shouldEqual("cart is empty")
      }
    }
    "respond with List(OrderCreateError) if amount ordered is more than in storage" in {
      cartAddPatch ~> authHeader ~> cartRoute
      ProductFactory.update(cartItem.productId, 1)
      Post("/orders", OrderPost("test")) ~> authHeader ~> orderRoute ~> check {
        status.shouldEqual(StatusCodes.Conflict)
        entityAs[List[OrderCreateError]].length.shouldEqual(1)
        entityAs[List[OrderCreateError]].head.productId.shouldEqual(cartItem.productId)
      }
    }
    "respond with 200 and clear cart and update product count" in {
      CartFactory.clear("npcrus@gmail.com")
      ProductFactory.update(cartItem.productId, 100)
      cartAddPatch ~> authHeader ~> cartRoute
      Post("/orders", OrderPost("test")) ~> authHeader ~> orderRoute ~> check {
        status.shouldEqual(StatusCodes.OK)
        entityAs[String].shouldEqual("1")
      }
      Get("/orders") ~> authHeader ~> orderRoute ~> check {
        entityAs[List[Order]].shouldEqual(List(
          Order(1, "npcrus@gmail.com", "test", List(cartItem))
        ))
      }
      Get("/cart") ~> authHeader ~> cartRoute ~> check {
        entityAs[List[CartItem]].shouldEqual(List.empty)
      }
    }
  }

  "OrderRoute get" should {
    "return list of orders" in {
      Get("/orders") ~> authHeader ~> orderRoute ~> check {
        entityAs[List[Order]].shouldEqual(List(Order(1, "npcrus@gmail.com", "test", List(cartItem))))
      }
    }
    "return list of orders that belong to specific email" in {
      OrderFactory.create("test@test.com", OrderPost("test"), List(cartItem))
      Get("/orders") ~> authHeader ~> orderRoute ~> check {
        entityAs[List[Order]].filterNot(o => o.email == "npcrus@gmail.com").length.shouldEqual(0)
      }
    }
  }
}
