import akka.actor.{Actor, ActorLogging, Props}



class DatabaseService extends Actor with ActorLogging with Database {

  override def receive: Receive = {

    case listOfInformation: List[String] =>
      if(!getUserAccountMap.contains(listOfInformation(3))) {
        val customerAccount = CustomerAccount(listOfInformation)
        addCustomerAccount(customerAccount.username, customerAccount)
        sender ! (customerAccount.username, "Account created successfully!")
      }
      else{
        sender() ! (listOfInformation(3), s"Username ${listOfInformation(3)} already exists! Try again with a different username")
      }

    case (accountNo: Long, billerName: String, billerCategory: Category.Value) =>
      val listOfBillers = getLinkedBiller.getOrElse(accountNo , Nil)
      if(listOfBillers.exists(_.billerCategory == billerCategory) || listOfBillers.isEmpty) {
        linkBiller(accountNo, billerName, billerCategory)
        sender() ! "Successfully Linked your account with the given biller!"
      }
      else
        {
          sender() ! "You are already linked to the given biller!"
        }

    case username: String => sender ! getUserAccountMap(username).accountNo

    case (accountNo: Long, customerName: String, salary: Double) =>
      depositSalary(accountNo, customerName, salary)

    case accountNo: Long => sender() ! getLinkedBiller.getOrElse(accountNo, Nil).map(_.billerCategory)

    case (accountNo: Long, billToPay: Double) => sender() ! payBill(accountNo, billToPay)
  }

}

object DatabaseService {

  def props: Props = Props(classOf[DatabaseService])

}