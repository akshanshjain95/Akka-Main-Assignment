import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration.DurationInt
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


class UserAccountService {

  def createAccount(listOfAccountInformation: List[List[String]], accountGeneratorActorRef: ActorRef): Future[Map[String,String]] = {

    implicit val timeout = Timeout(10 seconds)

    val listOfResult = for{
      accountInformation <- listOfAccountInformation
      account = (accountGeneratorActorRef ? accountInformation).mapTo[(String, String)]
    } yield account

    Future.sequence(listOfResult).map(_.toMap)

  }

  def linkBillerToAccount(accountNo: Long, billerName: String, billerCategory: Category.Value, linkedBillerToAccountRef: ActorRef): Future[String] = {

    implicit val timeout = Timeout(10 seconds)
    (linkedBillerToAccountRef ? (accountNo, billerName, billerCategory)).mapTo[String]

  }

}
