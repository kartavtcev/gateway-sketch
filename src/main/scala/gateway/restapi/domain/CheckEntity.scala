package gateway.restapi.domain

import gateway.restapi.domain.TransactionCurrency.TransactionCurrency

case class CheckModel(currency: String, amount: String)
case class CheckEntity(currency: TransactionCurrency, amount: BigDecimal)

object CheckModelConverter{
  def ModelToEntity(model : CheckModel) : CheckEntity = {
    CheckEntity(
      TransactionCurrency
        .withNameOpt(model.currency)
        .getOrElse(TransactionCurrency.USD),
      BigDecimal(model.amount))
  }
  def EntityToModel(entity : CheckEntity) : CheckModel = {
    CheckModel(
      entity.currency.toString,
      entity.amount.toString)
  }

  def OptStringToCurrency(currency : Option[String]) : TransactionCurrency = {
    TransactionCurrency.withNameOpt(currency.getOrElse(TransactionCurrency.USD.toString)).getOrElse(TransactionCurrency.USD)
  }
}