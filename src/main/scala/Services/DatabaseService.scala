import Models.{Category, CustomerAccount}
import akka.actor.{Actor, ActorLogging, Props}



class DatabaseServiceActor(database: Database) extends Actor with ActorLogging {

  override def receive: Receive = {

    case listOfInformation: List[String] =>
      if(!database.getUserAccountMap.contains(listOfInformation(3))) {
        val customerAccount = CustomerAccount(listOfInformation)
        database.addCustomerAccount(customerAccount.username, customerAccount)
        sender ! (customerAccount.username, "Account created successfully!")
      }
      else{
        sender() ! (listOfInformation(3), s"Username ${listOfInformation(3)} already exists! Try again with a different username")
      }

    case (accountNo: Long, billerName: String, billerCategory: Category.Value) =>
      val listOfBillers = database.getLinkedBiller.getOrElse(accountNo , Nil)
      if(!listOfBillers.exists(_.billerCategory == billerCategory) || listOfBillers.isEmpty) {
        database.linkBiller(accountNo, billerName, billerCategory)
        sender() ! "Successfully Linked your account with the given biller!"
      }
      else
        {
          sender() ! "You are already linked to the given biller!"
        }

    case username: String => sender ! database.getUserAccountMap(username).accountNo

    case (accountNo: Long, customerName: String, salary: Double) =>
      log.info("Depositing salary. Currently in DatabaseServiceActor")
      database.depositSalary(accountNo, customerName, salary)
      sender() ! "Salary deposited successfully!"

    case accountNo: Long => sender() ! database.getLinkedBiller.getOrElse(accountNo, Nil).map(_.billerCategory)

    case (accountNo: Long, billerCategory: Category.Value) => log.info("Sender in database Service is " + sender())
      val resultBool = database.payBill(accountNo, billerCategory)
      log.info("Received return as " + resultBool + " and seding it to sender " + sender())
      sender() ! resultBool
  }

}

object DatabaseServiceActor {

  def props(database: Database): Props = Props(classOf[DatabaseServiceActor], database)

}
