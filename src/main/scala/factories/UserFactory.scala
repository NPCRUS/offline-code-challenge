package factories

import models.{NextId, User, UserPost}

object UserFactory extends NextId[User] {
  var users: List[User] = List.empty

  def get: List[User] = users.reverse

  def getById(id: Long): Option[User] = users.find(_.id == id)

  def create(userPost: UserPost): Option[Long] = {
    def inc: Long = {
      users.lastOption match {
        case Some(user) => user.id + 1
        case None => 1
      }
    }

    users.find(_.email == userPost.email) match {
      case Some(_) => None
      case None =>
        val user: User = User.fromPost(nextId(users), userPost)
        users = user :: users
        Some(user.id)
    }
  }
}
