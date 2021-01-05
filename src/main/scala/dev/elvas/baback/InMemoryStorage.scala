package dev.elvas.baback

import scala.collection.mutable.Map

class InMemoryStorage extends Storage {
  val stacks = Map[String, List[Note]]()

  override def save(stackName: String, note: Note): Unit = (stacks get stackName) match {
    case Some(stack) => stacks += (stackName -> (note :: stack))
    case None => stacks += (stackName -> List(note))
  }

  override def list(stackName: String, filterQuery: Option[String]): List[Note] = {
    val stack = stacks(stackName)

    filterQuery match {
      case Some(f) => stack.filter(_.text.contains(f))
      case None => stack
    }
  }

  override def read(stackName: String, noteId: String): Note = stacks(stackName).filter(_.id == noteId).head
}
