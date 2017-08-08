package Services

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestActor, TestKit, TestProbe}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, FunSuite, FunSuiteLike}
import scala.concurrent.ExecutionContext.Implicits.global



class SalaryDepositServiceTest extends TestKit(ActorSystem("test-system")) with FunSuiteLike
  with BeforeAndAfterAll with ImplicitSender with MockitoSugar {

  val salaryDepositorActorProbe = TestProbe()
  val salaryDepositServiceObj = new SalaryDepositService

  override protected def afterAll(): Unit = {
    system.terminate()
  }

  test("Testing SalaryDepositService depositSalary method")
  {

    salaryDepositorActorProbe.setAutoPilot((sender: ActorRef, msg: Any) => {
      val resturnMsg = msg match {
        case (accountNo: Long, customerName: String, salary: Double) => true
      }
      sender ! resturnMsg
      TestActor.KeepRunning
    })

    val futureOfBoolean = salaryDepositServiceObj.depositSalary(1L, "Akshansh", 50000.00, salaryDepositorActorProbe.ref)

    futureOfBoolean.map(boolean => assert(boolean == true))
  }
}
