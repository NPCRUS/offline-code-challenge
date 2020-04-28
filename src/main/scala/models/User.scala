package models

object User {
  def fromPost(id: Long, post: UserPost): User =
    User(id, post.name, post.bankAccount, post.email)
}

case class User(
 id: Long,
 name: String,
 bankAccount: String,
 email: String
)

case class UserPost(
  name: String,
  bankAccount: String,
  email: String
)
