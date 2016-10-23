package gateway.restapi.services

import gateway.restapi.domain._
import gateway.restapi.domain.context.StorageContext
import gateway.restapi.utils.PredefGateway

class TransactionsService(context: StorageContext, walletsService: WalletsService)
{
  def processTransaction(model: TransactionModel): Option[TransactionEntity] = {

    var entity = TransactionConverter.ModelToEntity(
      model,
      context.uuid,
      Option(TransactionStatus.Accepted))

    PredefGateway.require(entity.check.amount >= 0, "check.amount >= 0")
    entity.paymentType match {
      case TransactionType.Sale | TransactionType.Refund => {
        PredefGateway.require(entity.issuer != None && entity.receiver != None, "both issuer and receiver must be present")
        PredefGateway.require(context.isValidClientUUID(entity.issuer) && context.isValidClientUUID(entity.receiver), "both issuer and receiver must be valid client id")
      }
      case TransactionType.TopUp | TransactionType.Withdraw => {
        PredefGateway.require(entity.issuer != None && entity.receiver == None, "only issuer must be present")
        PredefGateway.require(context.isValidClientUUID(model.issuer), "issuer must be valid client id")
      }
    }

    val clientIdToCheck = entity.paymentType match {
      case TransactionType.Sale | TransactionType.Withdraw =>
        entity.issuer
      case TransactionType.Refund =>
        entity.receiver
      case TransactionType.TopUp =>
        None
    }

    if(clientIdToCheck != None) {
      val wallet = walletsService.getTotalByClient(clientIdToCheck.get, entity.check.currency)

      //PredefGateway.require(wallet.total > entity.check.amount, s"client's total ${wallet.total} by currency ${wallet.currency} is less than required by transaction")
      if(wallet.total < entity.check.amount) {
        entity = TransactionConverter.UpdateEntityStatus(entity, TransactionStatus.Rejected)
      }
    }

    // todo: some transaction processing

    context.addTransaction(entity)
  }
}