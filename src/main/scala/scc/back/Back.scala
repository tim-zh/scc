package scc.back

import java.util.UUID

import scala.collection.mutable
import scala.concurrent.Future

trait Back {
	def addJob(workerJs: String): Future[String]

	def getJob(id: String): Future[Option[JobInfo]]

	def addWorker(jobId: String): Future[Option[(JobInfo, Int)]]

	def addMessageToMaster(jobId: String, msg: String): Future[Option[String]]

	def addMessageToWorker(jobId: String, workerId: Int, msg: String): Future[Option[String]]
}

class BackImpl extends Back {
	private val storage = new mutable.HashMap[String, JobInfo]

	override def addJob(workerJs: String): Future[String] = {
		val jobid = UUID.randomUUID().toString
		storage.put(jobid, JobInfo(jobid, workerJs, 0))
		Future.successful(jobid)
	}

	override def getJob(id: String): Future[Option[JobInfo]] = Future.successful(storage.get(id))

	override def addWorker(jobId: String): Future[Option[(JobInfo, Int)]] =
		Future.successful(storage.get(jobId) map { job =>
			job.workers += 1
			(job, job.workers - 1)
		})

	override def addMessageToMaster(jobId: String, msg: String): Future[Option[String]] =
		Future.successful(storage.get(jobId) match {
			case Some(info) =>
				info.addMsgToMaster(msg)
				None
			case None => Some("Wrong job id")
		})

	override def addMessageToWorker(jobId: String, workerId: Int, msg: String): Future[Option[String]] =
		Future.successful(storage.get(jobId) match {
			case Some(info) =>
				info.addMsgToWorker(workerId, msg)
				None
			case None => Some("Wrong job id")
		})
}