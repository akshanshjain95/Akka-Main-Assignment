import akka.actor.{Actor, ActorLogging, Props, Terminated}
import akka.dispatch.{BoundedMessageQueueSemantics, RequiresMessageQueue}
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}
import DatabaseService._


class AccountGeneratorActorMaster extends Actor with ActorLogging
  with RequiresMessageQueue[BoundedMessageQueueSemantics] {

  var accountNumber = 0

  var router = {
    log.info("Creating AccountGenerator routees")
    val FIVE = 5
    val accountGeneratorRoutees = Vector.fill(FIVE) {
      val routeeAccountGenerator = context.actorOf(Props[AccountGeneratorActor])
      context watch routeeAccountGenerator
      ActorRefRoutee(routeeAccountGenerator)
    }
    Router(RoundRobinRoutingLogic(), accountGeneratorRoutees)
  }

  override def receive: PartialFunction[Any, Unit] = {

    case customerInformation: List[String] =>
      if(!checkUsername(customerInformation(2))){
      log.info("Sending customer " +
      "information with account number to child to create the account")
      val listOfInformation = (accountNumber + 1).toString :: customerInformation
      router.route(listOfInformation, sender())}
      else{
        log.info("Existing username used.")
        sender() ! "Username already exists! Try again with a different username"
      }

    case Terminated(accountGeneratorActor) =>
      log.info("Terminating and re-creating AccountGenerator routee")
      router = router.removeRoutee(accountGeneratorActor)
      val routeeAccountGenerator = context.actorOf(Props[AccountGeneratorActor])
      context watch routeeAccountGenerator
      router = router.addRoutee(routeeAccountGenerator)

    case _ => log.info("invalid list received")
      sender() ! "Invalid information!"
  }
}


class AccountGeneratorActor extends Actor with ActorLogging {

  override def receive: Receive = {

    case listOfInformation: (List[String]) => log.info("Creating User Account")
      sender() ! CustomerAccount(listOfInformation)

    case _ => log.info("Was not able to create user account since invalid information received")
      sender() ! "Invalid information!"
  }
}

object AccountGeneratorActorMaster {

  def props: Props = Props[AccountGeneratorActorMaster]

}
