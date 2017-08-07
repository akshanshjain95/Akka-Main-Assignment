import Models.{Category, CustomerAccount, LinkedBiller}
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike}
import org.mockito.Mockito._

import scala.collection.mutable
import scala.collection.mutable.ListBuffer



class DatabaseServiceActorTest extends TestKit(ActorSystem("test-system")) with FunSuiteLike
  with BeforeAndAfterAll with ImplicitSender with MockitoSugar {

  val database = mock[Database]
  val databaseServiceActor = system.actorOf(DatabaseServiceActor.props(database))

  val linkedBiller: mutable.Map[Long, ListBuffer[LinkedBiller]] = mutable.Map(
    1L -> ListBuffer(
      LinkedBiller(Category.phone, "PhoneBiller", 1L, "date", 0.00, 0, 0, 0.00),
      LinkedBiller(Category.internet, "InternetBiller", 1L, "date", 0.00, 0, 0, 0.00)
    ),
    2L -> ListBuffer(
      LinkedBiller(Category.electricity, "ElectricityBiller", 2L, "date", 0.00, 0, 0, 0.00),
      LinkedBiller(Category.food, "FoodBiller", 2L, "date", 0.00, 0, 0, 0.00)
    )
  )

  test("Testing DatabaseServiceActor for creating an account")
  {

    val listOfInformation = List("1", "Akshansh", "Noida", "AkshanshJain1995", "10.00")

    when(database.getUserAccountMap).thenReturn(mutable.Map(
      "Akshansh95Jain" -> CustomerAccount(1L, "Akshansh", "B-62, Sector-56, Noida", "Akshansh95Jain", 10.00),
      "Rahul209" -> CustomerAccount(2L, "Rahul", "B-63, Sector-56, Noida", "Rahul209", 0.00)
    ))

    val customerAccount = CustomerAccount(listOfInformation)
    doNothing().when(database).addCustomerAccount(customerAccount.username , customerAccount)

    databaseServiceActor ! listOfInformation

    expectMsgPF() {

      case (username: String, msg: String) => assert(username == customerAccount.username &&
      msg == "Account created successfully!")

    }

  }

  test("Testing DatabaseServiceActor for creating an account with existing username")
  {

    val listOfInformation = List("1", "Akshansh", "Noida", "Akshansh95Jain", "10.00")

    when(database.getUserAccountMap).thenReturn(mutable.Map(
      "Akshansh95Jain" -> CustomerAccount(1L, "Akshansh", "B-62, Sector-56, Noida", "Akshansh95Jain", 10.00),
      "Rahul209" -> CustomerAccount(2L, "Rahul", "B-63, Sector-56, Noida", "Rahul209", 0.00)
    ))

    databaseServiceActor ! listOfInformation

    expectMsgPF() {

      case (username: String, msg: String) => assert(username == "Akshansh95Jain" &&
        msg == "Username Akshansh95Jain already exists! Try again with a different username")

    }

  }

  test("Testing DatabaseServiceActor for linking account to biller")
  {

   when(database.getLinkedBiller).thenReturn(linkedBiller)

    doNothing().when(database).linkBiller(100L, "TestingBiller", Category.phone)

    databaseServiceActor ! (100L, "TestingBiller", Category.phone)

    expectMsg("Successfully Linked your account with the given biller!")

  }

  test("Testing DatabaseServiceActor for linking where link already exists")
  {

    when(database.getLinkedBiller).thenReturn(linkedBiller)

    databaseServiceActor ! (1L, "TestingBiller", Category.phone)

    expectMsg("You are already linked to the given biller!")

  }

  test("Testing DatabaseServiceActor for returning account number when username is given")
  {

    val userAccountMap: mutable.Map[String, CustomerAccount] = mutable.Map(
      "Akshansh95Jain" -> CustomerAccount(1L, "Akshansh", "B-62, Sector-56, Noida", "Akshansh95Jain", 10.00),
      "Rahul209" -> CustomerAccount(2L, "Rahul", "B-63, Sector-56, Noida", "Rahul209", 0.00)
    )

    when(database.getUserAccountMap).thenReturn(userAccountMap)

    databaseServiceActor ! "Akshansh95Jain"

    expectMsgPF() {
      case accountNo: Long => assert(accountNo == 1L)
    }

  }

  test("Testing DatabaseServiceActor for depositing salary")
  {

    doNothing().when(database).depositSalary(1L, "Akshansh", 50000.00)

    databaseServiceActor ! (1L, "Akshansh", 50000.00)

    expectMsg("Salary deposited successfully!")

  }

  test("Testing DatabaseServiceActor for returning list of billerCategory for a given account number")
  {

    when(database.getLinkedBiller).thenReturn(linkedBiller)

    databaseServiceActor ! (1L)

    expectMsgPF() {

      case billerCategoryList: Seq[Category.Value] => assert(billerCategoryList(0) == Category.phone &&
      billerCategoryList(1) == Category.internet)

    }

  }

  test("Testing DatabaseServiceActor for paying bill using account number")
  {

    when(database.payBill(1L, 500.00, Category.phone)).thenReturn(true)

    databaseServiceActor ! (1L, 500.00, Category.phone)

    expectMsg(true)

  }

}
