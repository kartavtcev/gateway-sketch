package gateway.restapi.services

import gateway.restapi.domain.ClientModelUpdate
import gateway.restapi.domain.context.Context
import gateway.restapi.domain.ClientEnitity

class ClientsService(context: Context)  {
  def getClients(): Seq[ClientEnitity] = context.getClients
  def getClientById(id: String): Option[ClientEnitity] = context.getClientById(id)
  def createClient(user: ClientEnitity): Option[ClientEnitity] =
    context.addClient(ClientModelUpdate(context.uuid).merge(user))
}