package gateway.restapi.http.routes

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.CirceSupport
import gateway.restapi.domain._
import gateway.restapi.services.{TransactionsService, WalletsService}
import io.circe.generic.auto._
import io.circe.syntax._

class GatewayRoute (val transactionsService: TransactionsService, val walletsService: WalletsService)
                   extends CirceSupport {

  // 1. use POST method here, because it's secure: http://blog.teamtreehouse.com/the-definitive-guide-to-get-vs-post
  // 2. when dealing with multiple fields of data in transaction: shift to object or collection of key/value POST
  // 3. allow transaction with amount == 0 for testing/debuging purposes

  import transactionsService._

  val route =
  pathPrefix("gateway") {
    path("process") {
      pathEndOrSingleSlash {
        post {
          parameters('paymenttype.as[String], 'issuer.as[String].?, 'receiver.as[String].?, 'currency.as[String].?, 'amount.as[String]) {
            (paymenttype, issuer, receiver, currency, amount) =>
              complete {
                val entity = processTransaction(
                  TransactionModel(
                    None,
                    issuer,
                    receiver,
                    None,
                    CheckModelConverter.OptStringToCurrency(currency).toString,
                    amount,
                    TransactionStatus.Pending.toString,
                    paymenttype)
                )
                val model = TransactionConverter.EntityToModel(entity)
                model.map(_.asJson)
              }
          }
        }
      }
    } ~
      path("wallet") {
        pathEndOrSingleSlash {
          post {
            parameters('clientId.as[String], 'currency.as[String].?) {
              (clientId, currency) =>
                complete {
                  val entity = walletsService.getTotalByClient(clientId, CheckModelConverter.OptStringToCurrency(currency))
                  val model = WalletConverter.EntityToModel(entity)
                  EncoderOps(model).asJson // since not an Option
                }
            }
          }
        }
      }
    // todo: transaction statuses external updates. like 2 phase auth with sms.
  }
}

