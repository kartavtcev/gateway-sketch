package gateway.restapi

import scala.concurrent.ExecutionContext
import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import gateway.restapi.domain.storagecontext.StorageContext
import gateway.restapi.http.HttpService
import gateway.restapi.services.{ClientsService, TransactionsService, WalletsService}
import gateway.restapi.utils.Config


object Main extends App with Config {
  implicit val actorSystem = ActorSystem("gateway-sketch-rest-api")
  implicit val executor: ExecutionContext = actorSystem.dispatcher
  implicit val log: LoggingAdapter = Logging(actorSystem, getClass)
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val clientsService = new ClientsService(StorageContext.instanceProd)
  val walletsService = new WalletsService(StorageContext.instanceProd)
  val transactionService = new TransactionsService(StorageContext.instanceProd, walletsService)

  val httpService = new HttpService(clientsService, transactionService, walletsService)

  Http().bindAndHandle(httpService.routes, httpHost, httpPort)
}

