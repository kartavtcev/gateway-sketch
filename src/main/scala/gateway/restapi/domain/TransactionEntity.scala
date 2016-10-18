package gateway.restapi.domain

import akka.http.scaladsl.model.DateTime
import gateway.restapi.domain.TransactionStatus.TransactionStatus
import gateway.restapi.domain.TransactionType.TransactionType
import gateway.restapi.utils.{GatewayException, PredefGateway}

// models are all Strings because REST API format is defined by docs, not by specific auto serializer / deserializer (marshaller/unmarshaller) for integrations
// also could have declared marshaller/unmarshaller
case class TransactionModel(
                             id: Option[String] = None,
                             issuer: Option[String] = None,
                             receiver: Option[String] = None,
                             date: Option[String] = None,
                             currency: String,
                             amount: String,
                             status: String,
                             paymentType: String)

case class TransactionModelTest(
                             currency: String,
                             amount: String,
                             status: String,
                             paymentType: String)

case class TransactionEntity(
                              id: String,
                              issuer: Option[String] = None,
                              receiver: Option[String] = None,
                              date: Option[DateTime] = None,
                              check: CheckEntity,
                              status: TransactionStatus,
                              paymentType: TransactionType) {
  PredefGateway.require(check.amount >= 0, "check.amount >= 0")
}

object TransactionConverter { // there should be a way to AUTOmatically map between classes, but for demo manual way is OK.
  def ModelToEntity(model: TransactionModel, uuid: String, lastStatus: Option[TransactionStatus] = None)
  : TransactionEntity = {
    TransactionEntity(
      uuid,
      model.issuer,
      model.receiver,
      Option(DateTime.now),
      CheckModelConverter.ModelToEntity(CheckModel(model.currency, model.amount)),
      lastStatus.getOrElse(TransactionStatus.withNameOpt(model.status).getOrElse(throw new GatewayException("No Transaction Status"))),
      TransactionType.withNameOpt(model.paymentType).getOrElse(throw new GatewayException("No Transaction Type")))
  }

  def UpdateEntityStatus(entity: TransactionEntity, lastStatus: TransactionStatus)
    : TransactionEntity = {
    TransactionEntity(
      entity.id,
      entity.issuer,
      entity.receiver,
      entity.date,
      entity.check,
      lastStatus,
      entity.paymentType)
  }


  def EntityToModel(entityOpt : Option[TransactionEntity]) : Option[TransactionModel] = {
    entityOpt match  {
      case Some(entity) => {
        val check = CheckModelConverter.EntityToModel (entity.check)
        val model = TransactionModel (
          Option (entity.id),
          entity.issuer,
          entity.receiver,
          Option (entity.date.toString),
          check.currency,
          check.amount,
          entity.status.toString,
          entity.paymentType.toString)
    Option (model)
    }
      case None => None
    }
  }
}