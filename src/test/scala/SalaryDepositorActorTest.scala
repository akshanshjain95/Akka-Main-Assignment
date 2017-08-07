import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, FunSuite, FunSuiteLike}
import org.scalatest.mockito.MockitoSugar



class SalaryDepositorActorTest extends TestKit(ActorSystem("test-system")) with FunSuiteLike
  with BeforeAndAfterAll with ImplicitSender with MockitoSugar {

  val databaseService = TestProbe()
  val billProcessingActor = TestProbe()
  val salaryDepositorActor = system.actorOf(SalaryDepositorActor.props(databaseService.ref))

  test("Testing SalaryDepositorActor")
  {

    salaryDepositorActor ! (100L, "TestingCustomer", 50000.00)

  }
}
