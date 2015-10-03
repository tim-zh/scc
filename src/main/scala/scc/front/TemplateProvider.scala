package scc.front

import scc.html

object TemplateProvider {
	def step1(): String = html.step1().body

	def step2(jobId: String, masterJs: String): String = html.step2(jobId, masterJs).body

	def worker(jobId: String, workerId: String, workerJs: String): String = html.worker(jobId, workerId, workerJs).body
}
