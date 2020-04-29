package models

case class Identifier(id: Long)

trait NextId[T <: Identifier] {
  def nextId(list: List[T]): Long = {
    list.headOption match {
      case Some(identifier) => identifier.id + 1
      case None => 1
    }
  }
}
