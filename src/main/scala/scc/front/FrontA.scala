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
	private val gzipJson = encodeResponse(Gzip) & respondWithMediaType(`application/json`)

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
					onSuccess(back.addJob(workerJs)) { jobId =>
						complete(TemplateProvider.step2(jobId, masterJs))
					}
				}
			}
		} ~
		path("job" / Segment / "workersList") { jobId =>
			(get & gzipJson) {
				onSuccess(back.getJob(jobId)) { job =>
					if (job.isDefined)
						complete(toJson(job.get.workers))
					else
						complete(StatusCodes.NotFound, "Wrong job id")
				}
			}
		} ~
		path("job" / Segment / "newWorker") { jobId =>
			(get & gzipHtml) {
				onSuccess(back.addWorker(jobId)) { workerId =>
					if (workerId.isDefined)
						complete(workerId.get)
					else
						complete(StatusCodes.NotFound, "Wrong job id")
				}
			}
		} ~
		path("job" / Segment / "messageList") { jobId =>
			(get & gzipJson) {
				complete("todo")
			}
		} ~
		path("job" / Segment / "message") { jobId =>
			(post & gzipJson) {
				formField("msg") { msg =>
					complete("todo")
				}
			}
		} ~
		path("job" / Segment / "worker"/ Segment / "heartBeat") { (jobId, workerId) =>
			get {
				complete("todo")
			}
		} ~
		path("job" / Segment / "worker"/ Segment / "messageList") { (jobId, workerId) =>
			(get & gzipJson) {
				complete("todo")
			}
		} ~
		path("job" / Segment / "worker"/ Segment / "message") { (jobId, workerId) =>
			(post & gzipJson) {
				formField("msg") { msg =>
					complete("todo")
				}
			}
		} ~
		path("job" / Rest) { jobId =>
			(get & gzipHtml) {
				onSuccess(back.getJob(jobId)) { job =>
					if (job.isDefined)
						complete(TemplateProvider.worker(jobId, job.get.workerJs))
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

	def toJson[T](x: Iterable[T]): String = "[" + x.map("'" + _.toString + "'").reduceOption(_ + "," + _).getOrElse("") + "]"
}