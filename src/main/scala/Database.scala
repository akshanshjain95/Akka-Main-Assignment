import java.text.SimpleDateFormat
import java.util.Calendar

import Models.{Category, CustomerAccount, LinkedBiller}
import org.apache.log4j.Logger

import scala.collection.mutable
import scala.collection.mutable.Map
import scala.collection.mutable.ListBuffer


class Database {

  val logger = Logger.getLogger(this.getClass)

  val dateFormat = new SimpleDateFormat("d-M-y")
  val currentDate = dateFormat.format(Calendar.getInstance().getTime())

  private val linkedBiller: mutable.Map[Long, ListBuffer[LinkedBiller]] = mutable.Map(
    1L -> ListBuffer(
      LinkedBiller(Category.phone, "PhoneBiller", 1L, currentDate, 0.00, 0, 0, 0.00),
      LinkedBiller(Category.internet, "InternetBiller", 1L, currentDate, 0.00, 0, 0, 0.00)
    ),
    2L -> ListBuffer(
      LinkedBiller(Category.electricity, "ElectricityBiller", 2L, currentDate, 0.00, 0, 0, 0.00),
      LinkedBiller(Category.food, "FoodBiller", 2L, currentDate, 0.00, 0, 0, 0.00)
    )
  )

  private val userAccountMap: mutable.Map[String, CustomerAccount] = mutable.Map(
    "Akshansh95Jain" -> CustomerAccount(1L, "Akshansh", "B-62, Sector-56, Noida", "Akshansh95Jain", 10.00),
    "Rahul209" -> CustomerAccount(2L, "Rahul", "B-63, Sector-56, Noida", "Rahul209", 0.00)
  )

  def getUserAccountMap: mutable.Map[String, CustomerAccount] = userAccountMap

  def addCustomerAccount(username: String, customerAccount: CustomerAccount): Unit = {

    userAccountMap += (username -> customerAccount)

  }

  def getLinkedBiller: mutable.Map[Long, ListBuffer[LinkedBiller]] = linkedBiller

  def linkBiller(accountNo: Long, billerName: String, billerCategory: Category.Value): Unit = {

    val listOfBillers = linkedBiller.getOrElse(accountNo, Nil)
    val linkedBillerCaseClass = LinkedBiller(accountNo, billerName, billerCategory)

    listOfBillers match {

      case listOfBillers: List[LinkedBiller] if !listOfBillers.isEmpty =>
        linkedBiller(accountNo) += linkedBillerCaseClass

      case Nil => linkedBiller += accountNo -> ListBuffer(linkedBillerCaseClass)

    }

  }

  def depositSalary(accountNo: Long, customerName: String, salary: Double): Unit = {
    logger.info("Depositing salary of " + salary)
    userAccountMap foreach {
      case (username, customerAccount) => logger.info("Username = " + username + " & Customer Account = " + customerAccount)
        if (customerAccount.accountNo == accountNo) {
          logger.info("Creating new case class and replacing old one")
          val newCustomerAccount = customerAccount.copy(initialAmount = customerAccount.initialAmount + salary)
          logger.info("New customer account is " + newCustomerAccount)
          userAccountMap(username) = newCustomerAccount
        }
        else {
          userAccountMap(username) = customerAccount
        }

    }

    logger.info("Currently database is as: " + userAccountMap)

  }

  def payBill(accountNo: Long, billToPay: Double, billerCategory: Category.Value): Boolean = {

    val initialAmount = userAccountMap.values.filter(_.accountNo == accountNo).map(_.initialAmount).toList
    logger.info("Amount in the account is " + initialAmount.head)
    if (initialAmount.head > billToPay)
    {
      logger.info("If condition satisfied in payBill")
      val linkedBillerCaseClass = linkedBiller(accountNo).filter(_.billerCategory == billerCategory).head
      val dateWhilePayingBill = dateFormat.format(Calendar.getInstance().getTime())
      val newlinkedBillerCaseClass = linkedBillerCaseClass.copy(transactionDate = dateWhilePayingBill,
        amount = billToPay, totalIterations = linkedBillerCaseClass.totalIterations + 1,
        executedIterations = linkedBillerCaseClass.executedIterations + 1, paidAmount = linkedBillerCaseClass.amount + billToPay
      )

      val listOfLinkedBiller = linkedBiller(accountNo)
      listOfLinkedBiller -= linkedBillerCaseClass
      listOfLinkedBiller += newlinkedBillerCaseClass
      linkedBiller(accountNo) = listOfLinkedBiller

      logger.info("LinkedBiller map is as: " + linkedBiller)

      userAccountMap foreach {
        case (username, customerAccount) =>
          if (customerAccount.accountNo == accountNo) {
            val newCustomerAccount = customerAccount.copy(initialAmount = customerAccount.initialAmount - billToPay)
            userAccountMap(username) = newCustomerAccount
          }
          else {
            userAccountMap(username) = customerAccount
          }
      }
      logger.info("Returning true")
      true
    }
    else {
      logger.info("Returning false")
      false
    }

  }

}
