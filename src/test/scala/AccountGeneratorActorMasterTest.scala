import akka.actor.ActorSystem
import org.scalatest.{FunSuite, FunSuiteLike}
import akka.testkit.{TestKit, ImplicitSender}
import org.scalatest.BeforeAndAfterAll


class AccountGeneratorActorMasterTest extends TestKit(ActorSystem("test-system")) with FunSuiteLike
                                              with BeforeAndAfterAll with ImplicitSender {


  override protected def afterAll(): Unit = {
    system.terminate()
  }

  test("Testing AccountGeneratorActor which should return CusomterAccount") {
    val props = AccountGeneratorActorMaster.props
    val ref = system.actorOf(props)

    ref ! List("Akshansh", "B-62, Sector-56, Noida", "AkshanshJain95", "10.00")

    expectMsgPF() {
      case customerAccount: CustomerAccount =>
        assert(customerAccount.accountNo > 0 && customerAccount.initialAmount == 10.00 &&
          customerAccount.address == "B-62, Sector-56, Noida" &&
          customerAccount.customerName == "Akshansh" &&
          customerAccount.username == "AkshanshJain95")
    }
  }

  test("Testing AccountGeneratorActor with existing username") {
    val props = AccountGeneratorActorMaster.props
    val ref = system.actorOf(props)

    ref ! List("Akshansh", "B-62, Sector-56, Noida", "Akshansh95Jain", "10.00")

    expectMsg("Username already exists! Try again with a different username")
  }

}
