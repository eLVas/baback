package space.elvas.baback

abstract class Storage {
  def save(stackName: String, note: Note): Unit
  def list(stackName: String, filter: Option[String]): List[Note]
  def read(stackName: String, noteId: String): Note
}
