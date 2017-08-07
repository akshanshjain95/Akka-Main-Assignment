package Services

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration.DurationInt
import scala.concurrent.Future


class SalaryDepositService {

  def depositSalary(accountNo: Long, customerName: String, salary: Double, salaryDepositorActorRef: ActorRef): Future[Boolean] = {

    implicit val timeout = Timeout(10 seconds)
    (salaryDepositorActorRef ? (accountNo, customerName, salary)).mapTo[Boolean]

  }

}
