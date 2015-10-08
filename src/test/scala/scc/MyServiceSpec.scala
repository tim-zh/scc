package scc

import org.scalatest.mock.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}
import scc.back.Back
import scc.front._
import spray.testkit.ScalatestRouteTest

class MyServiceSpec extends FlatSpec with ScalatestRouteTest with Matchers with MockitoSugar with HttpApi with BackRef {
  override val back: Back = mock[Back]

  def actorRefFactory = system

  "Front" should "respond to heartbeat" in {
    Get("/job/123/worker/456/heartBeat") ~> route ~> check {
      responseAs[String] should equal("ok")
    }
  }
}
