package scc.back

import java.util.UUID

import scala.collection.mutable
import scala.concurrent.Future

trait Back {
	def set(value: String): Future[String]

	def get(id: String): Future[Option[String]]
}

class BackImpl extends Back {
	private val storage = new mutable.HashMap[String, String]

	override def set(value: String): Future[String] = {
		val id = UUID.randomUUID().toString
		storage.put(id, value)
		Future.successful(id)
	}

	override def get(id: String): Future[Option[String]] = Future.successful(storage.get(id))
}