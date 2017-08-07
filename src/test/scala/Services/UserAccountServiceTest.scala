package Services

import Models.Category
import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestActor, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike}
import scala.concurrent.ExecutionContext.Implicits.global




class UserAccountServiceTest extends TestKit(ActorSystem("test-system")) with FunSuiteLike
  with BeforeAndAfterAll with ImplicitSender {

  val userAccountServiceObject = new UserAccountService

  override protected def afterAll(): Unit = {
    system.terminate()
  }

  test("Testing createAccount for UserAccountService")
  {
    val probe = TestProbe()
    probe.setAutoPilot((sender: ActorRef, msg: Any) => {
      val resturnMsg = msg match {
        case listOfAccountInformation: List[String] => (listOfAccountInformation(2), "Account created successfully!")
      }
      sender ! (resturnMsg)
      TestActor.KeepRunning
    })
    val listOAccountInformaion = List(List("Akshansh", "B-62, Sector-56, Noida", "AkshanshJain95", "10.00"),
      List("Akshansh", "B-62, Sector-56, Noida", "Akshansh95Jain", "10.00"))
    userAccountServiceObject.createAccount(listOAccountInformaion, probe.ref).map(result =>
      assert(
      result == Map(
        "AkshanshJain95" -> "Account created successfully!", "Akshansh95Jain" -> "Account created successfully!"
      )
    ))
  }

  test("Testing linking of biller and account")
  {
    val probe = TestProbe()
    probe.setAutoPilot((sender: ActorRef, msg: Any) => {
      val resturnMsg = msg match {
        case (accountNo: Long, billerName: String, billerCategory: Category.Value) => "Successfully Linked your account with the given biller!"
      }
      sender ! resturnMsg
      TestActor.KeepRunning
    })

    userAccountServiceObject.linkBillerToAccount(3L, "CarBiller", Category.car, probe.ref).map(
      resutlMsg => assert(resutlMsg == "Successfully Linked your account with the given biller!")
    )

  }

}
