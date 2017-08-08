import Models.Category
import akka.actor.{ActorLogging, ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActor, TestKit, TestProbe}
import org.apache.log4j.Logger
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike, AsyncFunSuite}
import org.mockito.Mockito._



class LinkBillerToAccountActorTest extends TestKit(ActorSystem("test-system")) with FunSuiteLike
  with BeforeAndAfterAll with ImplicitSender with MockitoSugar {

  val databaseServiceProbe = TestProbe()
  val linkedBillerToAccountActorRef: ActorRef = system.actorOf(LinkBillerToAccountActor.props(databaseServiceProbe.ref))

  override protected def afterAll(): Unit = {
    system.terminate()
  }

  test("Testing LinkBillerToAccountActor and linking an account with a biller")
  {
    databaseServiceProbe.setAutoPilot((sender: ActorRef, msg: Any) => {
      val resturnMsg = msg match {
        case (accountNo: Long, billerName: String, billerCategory: Category.Value) => "Successfully Linked your account with the given biller!"
      }
      sender ! resturnMsg
      TestActor.NoAutoPilot
    })

    linkedBillerToAccountActorRef ! (20L, "TestingBiller", Category.car)

    expectMsg("Successfully Linked your account with the given biller!")

  }

  test("Testing LinkBillerToAccountActor with a link already existing")
  {
    databaseServiceProbe.setAutoPilot((sender: ActorRef, msg: Any) => {
      val resturnMsg = msg match {
        case (accountNo: Long, billerName: String, billerCategory: Category.Value) => "You are already linked to the given biller!"
      }
      sender ! resturnMsg
      TestActor.NoAutoPilot
    })

    linkedBillerToAccountActorRef ! (20L, "TestingBiller", Category.car)

    expectMsg("You are already linked to the given biller!")

  }

  test("Testing LinkBillerToAccountActor for invalid information")
  {
    linkedBillerToAccountActorRef ! (1, 2, 3)

    expectMsg("Invalid information received while linking!")
  }

}
