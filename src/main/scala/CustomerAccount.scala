case class CustomerAccount(accountNo: Long, customerName: String,
                           address: String, username: String, initialAmount: Double)


object CustomerAccount {

  def apply(listOfInformation: List[String]): CustomerAccount = {

    val ONE = 1
    val TWO = 2
    val THREE = 3
    val FOUR = 4
    
    CustomerAccount(listOfInformation.head.toLong, listOfInformation(ONE), listOfInformation(TWO),
      listOfInformation(THREE), listOfInformation(FOUR).toDouble)
  }

}
