import Models.Category
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern.{ask, pipe}
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
      log.info("Sender before ask is " + sender())
      val listOfBillers = (databaseServiceActor ? accountNo).mapTo[mutable.ListBuffer[Category.Value]]
      log.info("Sender after is " + sender())
      val senderInSalaryDepositor = sender().actorRef
      listOfBillers onComplete {

        case Success(value) => log.info("Sender in onComplete is " + senderInSalaryDepositor)
          value.foreach(billerCategory =>
            context.actorOf(BillProcessingActor.props(databaseServiceActor)).tell((accountNo, billerCategory), senderInSalaryDepositor))

        case Failure(ex) => log.info("Failed while receiving listOfBillers with exception " + ex)

      }
  }
}

class BillProcessingActor(databaseServiceActorRef: ActorRef) extends Actor with ActorLogging {

  override def receive: Receive = {

    case (accountNo: Long, billerCategory: Category.Value) =>
      log.info("Recieved by BillProcessingActor and the sender is " + sender())
      billerCategory match {

        case Category.car => databaseServiceActorRef.forward(accountNo, Category.car)
        case Category.phone => databaseServiceActorRef.forward(accountNo, Category.phone)
        case Category.internet => databaseServiceActorRef.forward(accountNo, Category.internet)
        case Category.electricity => databaseServiceActorRef.forward(accountNo, Category.electricity)
        case Category.food => databaseServiceActorRef.forward(accountNo, Category.food)

      }

    case _ => sender() ! "Invalid information received"

  }

}

object BillProcessingActor {

  def props(databaseService: ActorRef): Props = Props(classOf[BillProcessingActor], databaseService)

}

object SalaryDepositorActor {

  def props(databaseService: ActorRef): Props = Props(classOf[SalaryDepositorActor], databaseService)

}
