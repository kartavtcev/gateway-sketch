package gateway.restapi.http

import akka.event.LoggingAdapter
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.stream.ActorMaterializer
import gateway.restapi.http.routes.{ClientsServiceRoute, GatewayRoute}
import gateway.restapi.services.{ClientsService, TransactionsService, WalletsService}
import gateway.restapi.utils.CorsSupport

import scala.concurrent.ExecutionContext

class HttpService(
                   usersService: ClientsService,
                   transactionsService: TransactionsService,
                   walletsService: WalletsService
                 ) (implicit executionContext: ExecutionContext, log: LoggingAdapter, materializer : ActorMaterializer) extends CorsSupport {

  val clientsRouter = new ClientsServiceRoute(usersService)
  val gatewayRouter = new GatewayRoute(transactionsService, walletsService)

  val exceptionHandler = ExceptionHandler {
    case reason: Throwable =>
      log.error(reason, reason.getMessage)
      extractUri { uri =>
        complete(HttpResponse(StatusCodes.NoContent, entity = reason.getMessage)) // todo: response with internal exception message in DEBUG only
      }

  }

  val routes: Route =
    logRequestResult("gateway-sketch-rest-api") {
      handleExceptions(exceptionHandler) {
        pathPrefix("v1") {
          corsHandler {
            clientsRouter.route ~
              gatewayRouter.route
          }
        }
      } // Route.seal()
    }
}

////A Route can be "sealed" using Route.seal, which relies on the in-scope RejectionHandler and
////ExceptionHandler instances to convert rejections and exceptions into appropriate HTTP responses for the client.
