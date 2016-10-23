package gateway.restapi.services

import gateway.restapi.domain.TransactionCurrency._
import gateway.restapi.domain.storagecontext.StorageContext
import gateway.restapi.domain.{TransactionEntity, TransactionStatus, TransactionType, WalletEntity}
import gateway.restapi.utils.PredefGateway

class WalletsService(context: StorageContext) {

  // todo: Now wallet is built dynamically to avoid transactions / collections sync (aka centralized transactions storage). Probably separate storage would improve performance.
  private def buildWalletByUserIdAndCurrency(clientId: String, currency: TransactionCurrency): WalletEntity =  {
    val emptyWallet = WalletEntity(clientId, currency, 0)
    def getSignedAmountForIssuer(transaction : TransactionEntity) =
      transaction.paymentType match {
        case TransactionType.Sale  =>
          - transaction.check.amount
        case TransactionType.Refund =>
          transaction.check.amount
        case TransactionType.TopUp =>
          transaction.check.amount
        case TransactionType.Withdraw =>
          - transaction.check.amount
      }

    val wallet = context.getTransactions
      .map(transaction => {
        if (transaction.status == TransactionStatus.Accepted
          && transaction.check.currency.equals(currency)
          && (
          transaction.issuer != None && transaction.issuer.get.equals(clientId)
            || transaction.receiver != None && transaction.receiver.get.equals(clientId)
          )) {
          if (transaction.issuer != None && transaction.issuer.get.equals(clientId))
            WalletEntity(clientId, currency, getSignedAmountForIssuer(transaction))
          else if (transaction.receiver != None && transaction.receiver.get.equals(clientId))
            WalletEntity(clientId, currency, - getSignedAmountForIssuer(transaction)) // receiver is None in TopUp, Withdraw
          else emptyWallet
        }
        else emptyWallet
      })
      .fold(emptyWallet) {(one, two) => WalletEntity(clientId, currency, one.total + two.total)}

    wallet
  }

  def getTotalByClient(clientId: String, currency: TransactionCurrency) : WalletEntity = {
    PredefGateway.require(context.isValidClientUUID(Option(clientId)), "issuer must be valid client id")
    buildWalletByUserIdAndCurrency(clientId, currency)
  }
}
