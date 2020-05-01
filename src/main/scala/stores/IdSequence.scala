package stores

import models.Entity

class IdSequence {
  protected def nextId(list: List[Entity]): Long =
    list.headOption match {
      case Some(entity) => entity.id + 1
      case None => 1
    }
}
