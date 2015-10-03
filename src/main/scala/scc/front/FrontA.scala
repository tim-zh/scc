package scc.front

import akka.actor.Actor
import akka.util.Timeout
import scc.back.Back
import spray.http.MediaTypes._
import spray.http.StatusCodes
import spray.httpx.encoding.Gzip
import spray.routing._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
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

	private val indexMasterSubmit =
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
		}

	private val workersManagement =
		path("job" / Segment / "workersList") { jobId =>
			(get & gzipJson) {
				onBack(back.getJob(jobId)) { job =>
					job.workers.toString
				}
			}
		} ~
		path("job" / Segment / "newWorker") { jobId =>
			(get & gzipHtml) {
				onBack(back.addWorker(jobId)) { pair =>
					TemplateProvider.worker(jobId, pair._2.toString, pair._1.workerJs)
				}
			}
		}

	private val masterMessages =
		path("job" / Segment / "messageList") { jobId =>
			(get & gzipJson) {
				parameter('fromId) { fromId =>
					onBack(back.getJob(jobId)) { job =>
						val lastMessageId = strToInt(fromId).getOrElse(-1)
						toJson(job.getAllMsgToMasterAfter(lastMessageId))
					}
				}
			}
		} ~
		path("job" / Segment / "message") { jobId =>
			(post & gzipJson) {
				formField("msg") { msg =>
					onSuccess(back.addMessageToMaster(jobId, msg)) { exceptionDesc =>
						if (exceptionDesc.isDefined)
							complete(StatusCodes.NotFound, exceptionDesc.get)
						else
							complete("")
					}
				}
			}
		}

	private val workersMessages =
		path("job" / Segment / "worker"/ Segment / "messageList") { (jobId, workerId) =>
			(get & gzipJson) {
				parameter('fromId) { fromId =>
					onBack(back.getJob(jobId)) { job =>
						val lastMessageId = strToInt(fromId).getOrElse(-1)
						val parsedWorkerId = strToInt(workerId).getOrElse(-1)
						toJson(job.getAllMsgToWorkerAfter(parsedWorkerId, lastMessageId))
					}
				}
			}
		} ~
		path("job" / Segment / "worker"/ Segment / "message") { (jobId, workerId) =>
			(post & gzipJson) {
				formField("msg") { msg =>
					val parsedWorkerId = strToInt(workerId).getOrElse(-1)
					onSuccess(back.addMessageToWorker(jobId, parsedWorkerId, msg)) { exceptionDesc =>
						if (exceptionDesc.isDefined)
							complete(StatusCodes.NotFound, exceptionDesc.get)
						else
							complete("")
					}
				}
			}
		}

	val route =
		indexMasterSubmit ~
		workersManagement ~
		masterMessages ~
		workersMessages ~
		path("job" / Segment / "worker"/ Segment / "heartBeat") { (jobId, workerId) =>
			get {
				//todo
				complete("ok")
			}
		} ~
		path("favicon.ico") {
			complete(StatusCodes.NotFound)
		} ~
		path("assets" / Rest) { path =>
			getFromResource("%s" format path)
		}

	def onBack[T](backCall: => Future[Option[T]], warning: String = "Wrong job id")(response: T => String) = {
		onSuccess(backCall) { result =>
			if (result.isDefined)
				complete(response(result.get))
			else
				complete(StatusCodes.NotFound, warning)
		}
	}

	def strToInt(s: String) = try
		Some(s.toInt)
	catch {
		case _: NumberFormatException => None
	}

	def toJson[T](x: Iterable[T]): String = "[" + x.map("\"" + _ + "\"").reduceOption(_ + "," + _).getOrElse("") + "]"
}