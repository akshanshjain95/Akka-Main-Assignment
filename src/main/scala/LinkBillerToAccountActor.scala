import Models.Category
import akka.actor.{Actor, ActorLogging, ActorRef, Props}



class LinkBillerToAccountActor(databaseServiceActor: ActorRef) extends Actor with ActorLogging {

  override def receive: Receive = {

    case (accountNo: Long, billerName: String, billerCategory: Category.Value) =>
      log.info("Forwarding to databaseServiceActor for linking")
      databaseServiceActor.forward(accountNo, billerName, billerCategory)

    case _ => log.info("Invalid information received")
      sender() ! "Invalid information received while linking!"

  }

}

object LinkBillerToAccountActor {

  def props(databaseService: ActorRef): Props = Props(classOf[LinkBillerToAccountActor], databaseService)

}
