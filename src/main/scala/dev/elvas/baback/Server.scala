package dev.elvas.baback

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn
import java.util.UUID

import akka.http.scaladsl.server.Directives

// collect your json format instances into a support trait:
trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val urlContentFormat = jsonFormat2(URLContent)
  implicit val noteFormat = jsonFormat6(Note.apply)
}

object Server extends Directives with JsonSupport {
  var notes: Storage = new InMemoryStorage
  val defaultStack = "master"

  def init = {
    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "Baback")
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext: ExecutionContextExecutor = system.executionContext



    val routes = concat(
     path("") {
        get {
          parameters("url", "stack".?) { (url, stack) =>
            val n = Note(UUID.randomUUID().toString, url)
            notes.save(stack.getOrElse(defaultStack), n)

            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>Saved url: <a href=$url>$url</a> </h1>"))
          }
        }
     },
     path("notes") {
       concat(
         get {
           val urls = notes.list(defaultStack, None).map(n => n.original)
           complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, urls.map(url => s"<h1>Saved url: <a href=$url>$url</a> </h1>").mkString("\n")))
         },
         post {
           entity(as[Note]) { input =>
             notes.save(defaultStack, input)
             complete(input)
           }
         }
       )
      }
    )

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(routes)

    println(s"Server online at http://localhost:8080/hello\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
