import java.text.SimpleDateFormat
import java.util.Calendar

import Models.{Category, CustomerAccount, LinkedBiller}
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, FunSuite, FunSuiteLike}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer


class DatabaseTest extends TestKit(ActorSystem("test-system")) with FunSuiteLike
  with BeforeAndAfterAll with ImplicitSender with MockitoSugar {

  val database = new Database

  val dateFormat = new SimpleDateFormat("d-M-y")
  val currentDate = dateFormat.format(Calendar.getInstance().getTime())

  val linkedBiller: mutable.Map[Long, ListBuffer[LinkedBiller]] = mutable.Map(
    1L -> ListBuffer(
      LinkedBiller(Category.phone, "PhoneBiller", 1L, currentDate, 100.00, 0, 0, 0.00),
      LinkedBiller(Category.internet, "InternetBiller", 1L, currentDate, 200.00, 0, 0, 0.00)
    ),
    2L -> ListBuffer(
      LinkedBiller(Category.electricity, "ElectricityBiller", 2L, currentDate, 300.00, 0, 0, 0.00),
      LinkedBiller(Category.food, "FoodBiller", 2L, currentDate, 400.00, 0, 0, 0.00)
    )
  )

  val userAccountMapTest: mutable.Map[String, CustomerAccount] = mutable.Map(
    "Akshansh95Jain" -> CustomerAccount(1L, "Akshansh", "B-62, Sector-56, Noida", "Akshansh95Jain", 10.00),
    "Rahul209" -> CustomerAccount(2L, "Rahul", "B-63, Sector-56, Noida", "Rahul209", 1000.00)
  )

  val customerAccount = CustomerAccount(100L, "John", "America", "JohnDoe", 0.00)

  override protected def afterAll(): Unit = {
    system.terminate()
  }

  test("Testing getUserAccountMap") {

    assert(database.getUserAccountMap == userAccountMapTest)

  }

  test("Test for adding a user to database") {

    assert(database.addCustomerAccount("JohnDoe", customerAccount))

  }

  test("Testing getLinkedBiller") {

    assert(database.getLinkedBiller == linkedBiller)

  }

  test("Testing linkBiller where user already has other billers linked with him") {

    assert(database.linkBiller(1L, "TestBiller", Category.electricity))

  }

  test("Testing linkBiller where user does not have any biller linked with him") {

    assert(database.linkBiller(10L, "TestBiller", Category.electricity))

  }

  test("Testing depositSalary where user doesn't exist in Database") {

    assert(!database.depositSalary(194L, "Akshansh", 50000.00))

  }

  test("Testing depositSalary where user exists in Database") {

    assert(database.depositSalary(2L, "Akshansh", 50000.00))

  }

  test("Testing payBill for a user in Database not linked to a service") {

    assert(!database.payBill(1L, Category.car))

  }

  test("Testing payBill where amount to be paid exceeds account balance") {

    assert(!database.payBill(1L, Category.phone))

  }

  test("Testing payBill where user can successfully pay the bill") {

    assert(database.payBill(2L, Category.electricity))

  }

}
