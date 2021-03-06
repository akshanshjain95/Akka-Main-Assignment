import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.dispatch.{BoundedMessageQueueSemantics, RequiresMessageQueue}



class AccountGeneratorActor(databaseServiceActor: ActorRef) extends Actor with ActorLogging
  with RequiresMessageQueue[BoundedMessageQueueSemantics] {

  var accountNumber = 2

  override def receive: PartialFunction[Any, Unit] = {

    case customerInformation: List[_] =>
      customerInformation.head match {
        case string: String =>
          log.info("Assigning account number and forwarding it to databaseServiceActor and currently account number is " + accountNumber)
          accountNumber += 1
          val listOfInformation: List[String] = ((accountNumber).toString :: customerInformation).map(_.toString)
          databaseServiceActor.forward(listOfInformation)

        case _ => log.info("invalid list received")
          sender() ! "Invalid information!"

      }

    case _ => log.info("invalid list received")
      sender() ! "Invalid information!"
  }
}

object AccountGeneratorActor {

  def props(databaseService: ActorRef): Props = Props(classOf[AccountGeneratorActor], databaseService)

}
