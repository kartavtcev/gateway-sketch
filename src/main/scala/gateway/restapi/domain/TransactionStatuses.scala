package gateway.restapi.domain

object TransactionStatus extends Enumeration {
  type TransactionStatus = Value
  val Accepted, Rejected, Pending = Value
  def withNameOpt(s: String): Option[Value] = values.find(_.toString.equalsIgnoreCase(s))
}

object TransactionType extends Enumeration {
  type TransactionType = Value
  val Sale, Refund, TopUp, Withdraw = Value
  def withNameOpt(s: String): Option[Value] = values.find(_.toString.equalsIgnoreCase(s))
}

object TransactionCurrency extends Enumeration {
  type TransactionCurrency = Value
  val USD, GBP, CNY = Value
  def withNameOpt(s: String): Option[Value] = values.find(_.toString.equalsIgnoreCase(s))
}