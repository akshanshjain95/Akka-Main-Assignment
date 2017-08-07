import java.text.SimpleDateFormat
import java.util.Calendar

import akka.actor.ActorRef

case class LinkedBiller( billerCategory: Category.Value, billerName: String, accountNumber: Long,
                         transactionDate: String, amount: Double, totalIterations: Int,
                         executedIterations: Int, paidAmount: Double)

object LinkedBiller {

  def apply(accountNo: Long, billerName: String, billerCategory: Category.Value): LinkedBiller = {

    val dateFormat = new SimpleDateFormat("d-M-y")
    val currentDate = dateFormat.format(Calendar.getInstance().getTime())

    LinkedBiller(billerCategory, billerName, accountNo, currentDate, 0.00, 0, 0, 0.00)

  }

}
