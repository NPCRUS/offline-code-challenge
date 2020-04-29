package factories

import models.{ User, UserPost}

object UserFactory {
  var users: List[User] = List.empty

  def get: List[User] = users.reverse

  def getByEmail(email: String): Option[User] = users.find(_.email == email)

  def create(userPost: UserPost): Option[Long] = {
    users.find(_.email == userPost.email) match {
      case Some(_) => None
      case None =>
        val user: User = User.fromPost(nextId, userPost)
        users = user :: users
        Some(user.id)
    }
  }

  private def nextId: Long = {
    users.headOption match {
      case Some(user) => user.id + 1
      case None => 1
    }
  }
}
