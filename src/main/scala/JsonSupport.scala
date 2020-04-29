import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import models._

object JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val userProtocol: RootJsonFormat[User] = jsonFormat4(User.apply)
  implicit val userPostProtocol: RootJsonFormat[UserPost] = jsonFormat3(UserPost)

  implicit val productProtocol: RootJsonFormat[Product] = jsonFormat4(Product.apply)
  implicit val productPostProtocol: RootJsonFormat[ProductPost] = jsonFormat3(ProductPost)
}
