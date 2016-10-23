package gateway.restapi.http

import akka.event.LoggingAdapter
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import gateway.restapi.http.routes.{ClientsServiceRoute, GatewayRoute}
import gateway.restapi.services.{ClientsService, TransactionsService, WalletsService}

import scala.concurrent.ExecutionContext

class HttpService(
                   usersService: ClientsService,
                   transactionsService: TransactionsService,
                   walletsService: WalletsService
                 ) (implicit executionContext: ExecutionContext, log: LoggingAdapter, materializer : ActorMaterializer) {

  val clientsRouter = new ClientsServiceRoute(usersService)
  val gatewayRouter = new GatewayRoute(transactionsService, walletsService)
  val router = new Router(clientsRouter, gatewayRouter)

  val routes: Route = router.BuildRoute
}

