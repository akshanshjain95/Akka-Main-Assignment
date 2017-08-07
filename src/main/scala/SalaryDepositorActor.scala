import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.collection.mutable
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

class SalaryDepositorActor(databaseServiceActor: ActorRef) extends Actor with ActorLogging {

  override def receive: Receive = {

    case (accountNo: Long, customerName: String, salary: Double) =>
      databaseServiceActor.forward(accountNo, customerName, salary)
      implicit val timeout = Timeout(10 seconds)
      val listOfBillers = (databaseServiceActor ? accountNo).mapTo[mutable.ListBuffer[Category.Value]]
      listOfBillers onComplete {

        case Success(value) =>log.info("Sender in onComplete is " + sender())
          value.foreach(billerCategory => context.actorOf(BillProcessingActor.props(databaseServiceActor)).forward(accountNo, billerCategory))

        case Failure(ex) => log.info("Failed while receiving listOfBillers with exception " + ex)

      }
  }
}

class BillProcessingActor(databaseServiceActorRef: ActorRef) extends Actor with ActorLogging {

  val CAR_BILL: Double = 100
  val PHONE_BILL: Double = 200
  val INTERNET_BILL: Double = 300
  val ELECTRICITY_BILL: Double = 400
  val FOOD_BILL: Double = 500

  override def receive: Receive = {

    case (accountNo: Long, billerCategory: Category.Value) =>
      billerCategory match {

        case Category.car => databaseServiceActorRef.forward(accountNo, CAR_BILL)
        case Category.phone => databaseServiceActorRef.forward(accountNo, PHONE_BILL)
        case Category.internet => databaseServiceActorRef.forward(accountNo, INTERNET_BILL)
        case Category.electricity => databaseServiceActorRef.forward(accountNo, ELECTRICITY_BILL)
        case Category.food => databaseServiceActorRef.forward(accountNo, FOOD_BILL)

      }
  }

}

object BillProcessingActor {

  def props(databaseService: ActorRef): Props = Props(classOf[BillProcessingActor], databaseService)

}

object SalaryDepositorActor {

  def props(databaseService: ActorRef): Props = Props(classOf[SalaryDepositorActor], databaseService)

}
