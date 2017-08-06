import akka.actor.{ActorRef, ActorSystem}
import org.scalatest.FunSuiteLike
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._


class AccountGeneratorActorMasterTest extends TestKit(ActorSystem("test-system")) with FunSuiteLike
  with BeforeAndAfterAll with ImplicitSender with MockitoSugar with DatabaseService {

  val databaseService: DatabaseService = mock[DatabaseService]
  val accountGeneratorActorMasterRef: ActorRef = system.actorOf(AccountGeneratorActorMaster.props(databaseService))

  override protected def afterAll(): Unit = {
    system.terminate()
  }

  test("Testing AccountGeneratorActor which should return CusomterAccount") {

    when(databaseService.checkUsername("AkshanshJain95")).thenReturn(false)
    accountGeneratorActorMasterRef ! List("Akshansh", "B-62, Sector-56, Noida", "AkshanshJain95", "10.00")

    expectMsgPF() {
      case (username: String, resultMsg: String) =>
        assert(username == "AkshanshJain95" &&
        resultMsg == "Account created successfully!")
    }
  }

  test("Testing AccountGeneratorActor with existing username") {

    when(databaseService.checkUsername("Akshansh95Jain")).thenReturn(true)

    accountGeneratorActorMasterRef ! List("Akshansh", "B-62, Sector-56, Noida", "Akshansh95Jain", "10.00")

    expectMsgPF() {
      case (username: String,resultMsg: String) => assert(username == "Akshansh95Jain" &&
      resultMsg == "Username Akshansh95Jain already exists! Try again with a different username")
    }
  }

  test("Testing AccountGeneratorActor with invalid list") {

    accountGeneratorActorMasterRef ! List(1,2,3,4,5)

    expectMsg("Invalid information!")
  }

}
