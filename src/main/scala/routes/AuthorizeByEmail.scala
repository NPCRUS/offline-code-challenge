package routes

import factories.UserFactory

trait AuthorizeByEmail {
  def authorizeByEmail(email: String): Boolean =
    UserFactory.getByEmail(email) match {
      case Some(_) => true
      case None => false
    }
}
