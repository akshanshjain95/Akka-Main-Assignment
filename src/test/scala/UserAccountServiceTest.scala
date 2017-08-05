import akka.actor.ActorSystem
import org.scalatest.FunSuiteLike
import akka.testkit.{TestKit, ImplicitSender}
import org.scalatest.BeforeAndAfterAll



class UserAccountServiceTest extends TestKit(ActorSystem("test-system")) with FunSuiteLike
  with BeforeAndAfterAll with ImplicitSender {

  override protected def afterAll(): Unit = {
    system.terminate()
  }

}
