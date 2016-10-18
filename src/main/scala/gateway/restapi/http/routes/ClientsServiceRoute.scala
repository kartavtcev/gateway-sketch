package gateway.restapi.http.routes

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.CirceSupport
import io.circe.generic.auto._
import io.circe.syntax._

import gateway.restapi.domain.ClientEnitity
import gateway.restapi.services.ClientsService

class ClientsServiceRoute(val clientsService: ClientsService) extends CirceSupport {

  import clientsService._

  val route = pathPrefix("clients") {
    pathEndOrSingleSlash {
      get {
        complete(getClients().map(_.asJson))
      } ~
        post {
          entity(as[ClientEnitity]) { client =>
            complete(createClient(client).map(_.asJson))
          }
        }
    } ~
      path(JavaUUID) { id =>
        pathEndOrSingleSlash {
          get {
            complete(getClientById(id.toString).map(_.asJson))
            //              } ~ // todo: no CRUD for this demo
            //                post {
            //                  entity(as[ClientEntity]) { clientUpdate =>
            //                    complete(updateClient(id, clientUpdate).map(_.asJson))
            //                  }
            //                } ~
            //                delete {
            //                  onSuccess(deleteClient(id)) { ignored =>
            //                    complete(NoContent)
            //                  }
            //                }
          }
        }
      }
  }
}
