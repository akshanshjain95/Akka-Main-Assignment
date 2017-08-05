import org.apache.log4j.Logger

object DatabaseService extends Database {

  def addToDatabase(username: String, customerAccount: CustomerAccount): Unit = {

    addCustomerAccount(username, customerAccount)

  }

  def checkUsername(username: String): Boolean = {

    val logger = Logger.getLogger(this.getClass)
    logger.info(s"Checking if user exists in the database with the username $username")
    if(userAccountMap.contains(username)) true else false

  }

}