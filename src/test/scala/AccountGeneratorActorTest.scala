import akka.actor.{ActorRef, ActorSystem}
import org.scalatest.FunSuiteLike
import akka.testkit.{ImplicitSender, TestActor, TestKit, TestProbe}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._


class AccountGeneratorActorTest extends TestKit(ActorSystem("test-system")) with FunSuiteLike
  with BeforeAndAfterAll with ImplicitSender with MockitoSugar {

  val databaseServiceProbe = TestProbe()
  val accountGeneratorActorRef: ActorRef = system.actorOf(AccountGeneratorActor.props(databaseServiceProbe.ref))

  override protected def afterAll(): Unit = {
    system.terminate()
  }

  test("Testing AccountGeneratorActor which should return map containing status message for each account") {

    val customerAccount = CustomerAccount(1L, "Akshansh", "B-62, Sector-56, Noida", "AkshanshJain95", 0.00)

    accountGeneratorActorRef ! List("Akshansh", "B-62, Sector-56, Noida", "AkshanshJain95", "10.00")

      databaseServiceProbe.setAutoPilot((sender: ActorRef, msg: Any) => {
        val resturnMsg = msg match {
          case listOfInformation: List[String] => (customerAccount.username, "Account created successfully!")
        }
        sender ! resturnMsg
        TestActor.KeepRunning
      })

    expectMsgPF() {
      case (username: String, resultMsg: String) =>
        assert(username == "AkshanshJain95" &&
        resultMsg == "Account created successfully!")
    }
  }

  test("Testing AccountGeneratorActor with existing username") {

    accountGeneratorActorRef ! List("Akshansh", "B-62, Sector-56, Noida", "Akshansh95Jain", "10.00")

    databaseServiceProbe.setAutoPilot((sender: ActorRef, msg: Any) => {
      val resturnMsg = msg match {
        case listOfInformation: List[String] => ("Akshansh95Jain", "Username Akshansh95Jain already exists! Try again with a different username")
      }
      sender ! resturnMsg
      TestActor.KeepRunning
    })

    expectMsgPF() {
      case (username: String,resultMsg: String) => assert(username == "Akshansh95Jain" &&
      resultMsg == "Username Akshansh95Jain already exists! Try again with a different username")
    }
  }

  test("Testing AccountGeneratorActor with invalid list") {

    accountGeneratorActorRef ! List(1,2,3,4,5)

    expectMsg("Invalid information!")
  }

}
