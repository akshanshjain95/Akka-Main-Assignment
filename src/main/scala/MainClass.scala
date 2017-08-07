import Services.SalaryDepositService
import akka.actor.{ActorSystem, Props}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


object MainClass extends App {

  val actorSystem = ActorSystem("AccountSystemActor")

  val databaseServiceActor = actorSystem.actorOf(DatabaseService.props)

  val accountGeneratorActor = actorSystem.actorOf(AccountGeneratorActor.props(databaseServiceActor))

  val linkBillerToAccountActor = actorSystem.actorOf(LinkBillerToAccountActor.props(databaseServiceActor))

  val salaryDepositorActor = actorSystem.actorOf(SalaryDepositorActor.props(databaseServiceActor))

  val listOAccountInformaion = List(List("Akshansh", "B-62, Sector-56, Noida", "AkshanshJain1995", "10.00"),
    List("Akshansh", "B-62, Sector-56, Noida", "Akshansh9195Jain", "10.00"))

  val userAccountServiceObj = new UserAccountService

  val mapOfAccounts = userAccountServiceObj.createAccount(listOAccountInformaion, accountGeneratorActor)

  mapOfAccounts.map(println(_))

  val resultString = userAccountServiceObj.linkBillerToAccount(3L, "PhoneBiller", Category.phone, linkBillerToAccountActor)

  resultString.map(println(_))

  val salaryDepositServiceObj = new SalaryDepositService

  val resultBool = salaryDepositServiceObj.depositSalary(3L, "Akshansh", 50000.00, salaryDepositorActor)

  resultBool.map(println(_))

}
