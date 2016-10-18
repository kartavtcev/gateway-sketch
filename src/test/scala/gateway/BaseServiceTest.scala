package gateway

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import de.heikoseeberger.akkahttpcirce.CirceSupport
import gateway.restapi.domain.ClientEnitity
import gateway.restapi.domain.context.Context
import gateway.restapi.http.HttpService
import gateway.restapi.services.{ClientsService, TransactionsService, WalletsService}

import org.scalatest._
import scala.util.Random

trait BaseServiceTest extends WordSpec with Matchers with ScalatestRouteTest with CirceSupport {

  implicit val actorSystem = ActorSystem("gateway-sketch-rest-api-test")
  implicit val log: LoggingAdapter = Logging(actorSystem, getClass)

  val clientsService = new ClientsService(Context.instance)
  var walletsService = new WalletsService(Context.instance)
  var transactionService = new TransactionsService(Context.instance, walletsService)
  val httpService = new HttpService(clientsService, transactionService, walletsService)

  def cleanContext : Unit = { Context.instance.clean() } // todo: replace this hack ro reset/clear Context with better language/scala test feature
  def provisionClientsList(size: Int): Seq[ClientEnitity] = {
    (1 to size).map { _ =>
      clientsService.createClient(ClientEnitity(None, Random.nextString(10)))
    }
    Context.instance.getClients
  }
  def getTransactionService = transactionService
}
