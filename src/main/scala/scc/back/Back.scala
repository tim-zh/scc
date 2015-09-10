package scc.back

import java.util.UUID

import scala.collection.mutable
import scala.concurrent.Future

trait Back {
	def addJob(workerJs: String): Future[String]

	def getJob(id: String): Future[Option[JobInfo]]

	def addWorker(jobId: String): Future[Option[String]]
}

class BackImpl extends Back {
	private val storage = new mutable.HashMap[String, JobInfo]

	override def addJob(workerJs: String): Future[String] = {
		val jobid = UUID.randomUUID().toString
		storage.put(jobid, JobInfo(jobid, workerJs, Seq[String]()))
		Future.successful(jobid)
	}

	override def getJob(id: String): Future[Option[JobInfo]] = Future.successful(storage.get(id))

	override def addWorker(jobId: String): Future[Option[String]] =
		Future.successful(storage.get(jobId) map { job =>
			val workerId = UUID.randomUUID().toString
			job.workers :+= workerId
			workerId
		})
}