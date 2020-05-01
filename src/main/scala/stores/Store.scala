package stores

import models.Entity

class Store[T <: Entity] {
  private var list: List[T] = List.empty

  def get(): List[T] = list.reverse

  def create(body: T): Long = {
    list = body :: list
    body.id
  }

  def update(body: T): Unit = {
    list = list.map(e => {
      if(e.id == body.id) body
      else e
    })
  }

  def nextId: Long = {
    list.headOption match {
      case Some(entity) => entity.id + 1
      case None => 1
    }
  }
}
