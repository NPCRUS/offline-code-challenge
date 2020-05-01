package routes

import models.User
import stores.Store

class AuthorizeByEmail(userStore: Store[User]) {
  def authorizeByEmail(email: String): Boolean = {
    userStore.get().find(_.email == email) match {
      case Some(_) => true
      case None => false
    }
  }
}
