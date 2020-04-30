package models

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

object JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val userProtocol: RootJsonFormat[User] = jsonFormat4(User.apply)
  implicit val userPostProtocol: RootJsonFormat[UserPost] = jsonFormat3(UserPost)

  implicit val productProtocol: RootJsonFormat[Product] = jsonFormat4(Product.apply)
  implicit val productPostProtocol: RootJsonFormat[ProductPost] = jsonFormat3(ProductPost)

  implicit val cartItemProtocol: RootJsonFormat[CartItem] = jsonFormat2(CartItem)
  implicit val cartItemDeleteProtocol: RootJsonFormat[CartItemDelete] = jsonFormat1(CartItemDelete)

  implicit val orderProtocol: RootJsonFormat[Order] = jsonFormat4(Order)
  implicit val orderPostProtocol: RootJsonFormat[OrderPost] = jsonFormat1(OrderPost)
  implicit val orderCreateError: RootJsonFormat[OrderCreateError] = jsonFormat2(OrderCreateError)
}
