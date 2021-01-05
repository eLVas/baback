package space.elvas.baback

import java.util.UUID

case class Note(
                 id: String,
                 original: String,
                 text: Option[String],
                 urls: Option[List[URLContent]],
                 tags: Option[List[String]],
                 status: Option[String]
               )

object Note {
  def apply(id: String, original: String): Note = new Note(id, original, None, None, None, None)
}
