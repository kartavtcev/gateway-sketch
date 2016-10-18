package gateway.restapi.domain

import gateway.restapi.domain.TransactionCurrency.TransactionCurrency

case class WalletEntity(userId: String, currency: TransactionCurrency, total: BigDecimal)

case class WalletModel(userId: String, currency: String, total: String)

object WalletConverter {
  def EntityToModel(entity: WalletEntity): WalletModel = {
    WalletModel(entity.userId, entity.currency.toString, entity.total.toString())
  }
}
