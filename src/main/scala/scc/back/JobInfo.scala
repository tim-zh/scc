package scc.back

import scala.collection.mutable

case class JobInfo(id: String,
									 workerJs: String,
									 var workers: Int,
									 masterInbox: mutable.LinkedHashMap[Int, String] = mutable.LinkedHashMap(),
									 workersInboxes: mutable.Map[Int, mutable.LinkedHashMap[Int, String]] = mutable.Map()) {
	def addMsgToMaster(msg: String) = masterInbox.put(masterInbox.size, msg)

	def getMsgToMaster(msgId: Int) = masterInbox.get(msgId)

	def getAllMsgToMasterAfter(msgId: Int) = masterInbox.filter(entry => entry._1 > msgId).values

	def addMsgToWorker(workerId: Int, msg: String) = {
		if (! workersInboxes.contains(workerId))
			workersInboxes.put(workerId, mutable.LinkedHashMap())
		workersInboxes(workerId).put(workersInboxes(workerId).size, msg)
	}

	def getMsgToWorker(workerId: Int, msgId: Int) = workersInboxes.get(workerId).flatMap(_.get(msgId))

	def getAllMsgToWorkerAfter(workerId: Int, msgId: Int) = workersInboxes.get(workerId).map(_.filter(entry => entry._1 > msgId).values).getOrElse(Nil)
}
