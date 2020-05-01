import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import models.{Order, Product, User}
import routes.{CartRoutes, OrderRoutes, ProductRoutes, UserRoutes}
import stores.{CartStore, Store}

object Router {
  val userStore = new Store[User]
  val productStore = new Store[Product]
  val cartStore = new CartStore
  val orderStore = new Store[Order]

  def apply(withSeeds: Boolean = false): Route = {
    if(withSeeds) seeds()
    concat(
      new UserRoutes(userStore).getRoutes,
      new ProductRoutes(productStore).getRoutes,
      new CartRoutes(cartStore, productStore, userStore).getRoutes,
      new OrderRoutes(orderStore, cartStore, productStore, userStore).getRoutes
    )
  }

  private def seeds(): Unit = {
    productStore.create(Product(productStore.nextId, "coca-cola", 2, 100))
    productStore.create(Product(productStore.nextId, "pepsi-cola", 1, 200))
    userStore.create(User(userStore.nextId, "Nikita", "DE152332432324", "npcrus@gmail.com"))
  }
}
