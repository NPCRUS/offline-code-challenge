package models

object User {
  def fromPost(id: Long, post: UserPost): User =
    User(id = id, name = post.name, bankAccount = post.bankAccount, email = post.email)
}

case class User(
 id: Long,
 name: String,
 bankAccount: String,
 email: String
) extends Entity

case class UserPost(
  name: String,
  bankAccount: String,
  email: String
)
