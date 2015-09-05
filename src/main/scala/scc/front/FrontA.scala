package scc.front

import akka.actor.Actor
import akka.util.Timeout
import scc.back.Back
import spray.http.MediaTypes._
import spray.http.StatusCodes
import spray.httpx.encoding.Gzip
import spray.routing._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class FrontA(val back: Back) extends Actor with HttpApi with BackRef {
  def actorRefFactory = context

  def receive = runRoute(route)
}

trait BackRef {
  val back: Back
}

trait HttpApi extends HttpService { this: BackRef =>
  private implicit val timeout = Timeout(5.seconds)
  private val gzipHtml = encodeResponse(Gzip) & respondWithMediaType(`text/html`)

  val route =
    path("") {
      redirect("step1", StatusCodes.MovedPermanently)
    } ~
    path("step1") {
      (get & gzipHtml) {
        complete(TemplateProvider.step1())
      }
    } ~
    path("step2") {
      (post & gzipHtml) {
        formFields("masterJs", "workerJs") { (masterJs, workerJs) =>
          onSuccess(back.set(workerJs)) { jobId =>
            complete(TemplateProvider.step2(jobId, masterJs))
          }
        }
      }
    } ~
    path("job" / Rest) { jobId =>
      (get & gzipHtml) {
        onSuccess(back.get(jobId)) { workerJs =>
          if (workerJs.isDefined)
            complete(TemplateProvider.worker(workerJs.get))
          else
            complete(StatusCodes.NotFound, "Wrong job id")
        }
      }
    } ~
    path("favicon.ico") {
      complete(StatusCodes.NotFound)
    } ~
    path("assets" / Rest) { path =>
      getFromResource("%s" format path)
    }
}