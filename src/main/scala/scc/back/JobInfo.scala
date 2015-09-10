package scc.back

case class JobInfo(id: String, workerJs: String, var workers: Seq[String])
