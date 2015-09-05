package scc.front

import akka.actor.{ActorSystem, Props, TypedActor, TypedProps}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import scc.back.{Back, BackImpl}
import spray.can.Http

import scala.concurrent.duration._

object Boot extends App {
  implicit val system = ActorSystem("as")
  implicit val timeout = Timeout(5.seconds)

  val back: Back = TypedActor(system).typedActorOf(TypedProps[BackImpl]())
  val front = system.actorOf(Props(classOf[FrontA], back))

  IO(Http) ? Http.Bind(front, interface = "localhost", port = 80)
}
