import scala.collection.mutable
import scala.collection.mutable.Map


trait Database {

  private val userAccountMap: mutable.Map[String, CustomerAccount] = Map(
    "Akshansh95Jain" -> CustomerAccount(1L, "Akshansh", "B-62, Sector-56, Noida", "Akshansh95Jain", 10.00),
    "Rahul209" -> CustomerAccount(2L, "Rahul", "B-63, Sector-56, Noida", "Rahul209", 0.00)
  )

  def getUserAccountMap: mutable.Map[String, CustomerAccount] = userAccountMap

  def addCustomerAccount(username: String, customerAccount: CustomerAccount): Unit = {

    userAccountMap += (username -> customerAccount)

  }

}
