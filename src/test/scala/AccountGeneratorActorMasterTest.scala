import akka.actor.ActorSystem
import org.scalatest.{FunSuite, FunSuiteLike}
import akka.testkit.{TestKit, ImplicitSender}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._
import DatabaseService._


class AccountGeneratorActorMasterTest extends TestKit(ActorSystem("test-system")) with FunSuiteLike
  with BeforeAndAfterAll with ImplicitSender with MockitoSugar {

  val props = AccountGeneratorActorMaster.props
  val ref = system.actorOf(props)

  override protected def afterAll(): Unit = {
    system.terminate()
  }

  test("Testing AccountGeneratorActor which should return CusomterAccount") {


    ref ! List("Akshansh", "B-62, Sector-56, Noida", "AkshanshJain95", "10.00")

    expectMsgPF() {
      case (username: String, resultMsg: String) =>
        assert(username == "AkshanshJain95" &&
        resultMsg == "Account created successfully!")
    }
  }

  test("Testing AccountGeneratorActor with existing username") {

    ref ! List("Akshansh", "B-62, Sector-56, Noida", "Akshansh95Jain", "10.00")

    expectMsgPF() {
      case (username: String,resultMsg: String) => assert(username == "Akshansh95Jain" &&
      resultMsg == "Username Akshansh95Jain already exists! Try again with a different username")
    }
  }

  test("Testing AccountGeneratorActor with invalid list") {

    ref ! List(1,2,3,4,5)

    expectMsg("Invalid information!")
  }

}
