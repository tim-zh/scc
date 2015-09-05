package scc.front

import scc.html

object TemplateProvider {
  def step1(): String = html.step1().body

  def step2(taskUrl: String, masterJs: String): String = html.step2(taskUrl, masterJs).body

  def worker(workerJs: String): String = html.worker(workerJs).body
}
