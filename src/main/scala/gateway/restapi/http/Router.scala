package gateway.restapi.http

import akka.event.LoggingAdapter
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.stream.ActorMaterializer
import scala.concurrent.ExecutionContext
import gateway.restapi.http.routes.{ClientsServiceRoute, GatewayRoute}
import gateway.restapi.utils.CorsSupport

class Router(clientsRouter: ClientsServiceRoute, gatewayRouter: GatewayRoute)
            (implicit executionContext: ExecutionContext, log: LoggingAdapter, materializer : ActorMaterializer)  extends CorsSupport {
  def BuildRoute: Route = {

    val exceptionHandler = ExceptionHandler {    // todo: response with internal exception message in DEBUG only
      case error: Throwable => {  // not a bad practice to catch Throwable here because it's the Root
        log.error(error, error.getMessage)
        extractUri { uri =>
          complete(HttpResponse(StatusCodes.BadRequest, entity = error.getMessage))
        }
      }
    }

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
    ////A Route can be "sealed" using Route.seal, which relies on the in-scope RejectionHandler and
    ////ExceptionHandler instances to convert rejections and exceptions into appropriate HTTP responses for the client.
  }
}
