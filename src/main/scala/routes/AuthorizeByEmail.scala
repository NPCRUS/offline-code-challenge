package routes

import stores.UserStore

trait AuthorizeByEmail {
  def authorizeByEmail(email: String): Boolean =
    UserStore.getByEmail(email) match {
      case Some(_) => true
      case None => false
    }
}
