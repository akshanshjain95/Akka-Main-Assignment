import akka.actor.{Actor, ActorLogging}



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
      if(listOfBillers.exists(_.billerCategory == billerCategory)) {
        linkBiller(accountNo, billerName, billerCategory)
        sender() ! "Successfully Linked your account with the given biller!"
      }
      else
        {
          sender() ! "You are already linked to the given biller!"
        }

    case username: String => getUserAccountMap(username).accountNo

  }

}
