package gateway.restapi.domain

import gateway.restapi.utils.PredefGateway

case class ClientEnitity(id: Option[String], name: String, isActive: Option[Boolean] = Option(true)) {
  PredefGateway.require(!name.isEmpty, "name.empty")
}

case class ClientModelUpdate(id: String) {
  def merge(user: ClientEnitity): ClientEnitity = {
    ClientEnitity(Option(id), user.name)
  }
}