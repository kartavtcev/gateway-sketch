package gateway.restapi.domain.context

import java.util.concurrent.ConcurrentHashMap

import scala.collection._
import scala.collection.convert.decorateAsScala._

import gateway.restapi.domain._

// todo: could split context to clients and transactions contexts to make separation of concerns cleaner
// todo: snapshot on each transaction to NoSql storage like MongoDB (good for JSON) or Cassandra (good for scalability / clusters)
sealed class Context private(){
  private val clients: concurrent.Map[String, ClientEnitity] = new ConcurrentHashMap[String, ClientEnitity]().asScala
  private val transactions: concurrent.Map[String, TransactionEntity] = new ConcurrentHashMap[String, TransactionEntity]().asScala

  def clean() = {
    clients.clear()
    transactions.clear()
  }

  def getClients() : Seq[ClientEnitity] = clients.values.toSeq
  def getClientById(id : String) : Option[ClientEnitity] = clients.get(id)

  def getTransactions() : Seq[TransactionEntity] = transactions.values.toSeq
  def getTransactionById(id: String) : Option[TransactionEntity] = transactions.get(id)

  def addClient(client: ClientEnitity) : Option[ClientEnitity] = {
    clients.put(client.id.get, client)
    clients.get(client.id.get)  // for the matter of ID / checking results, return to user a new entity
  }
  def addTransaction(transaction: TransactionEntity) : Option[TransactionEntity] = {  // todo: could log transaction as well
    transactions.put(transaction.id, transaction)
    transactions.get(transaction.id)  // for the matter of ID / checking results, return to user a new entity
  }

  def uuid = java.util.UUID.randomUUID.toString
  def isValidClientUUID(id : Option[String]) : Boolean = {
    if(!id.exists(_.trim.nonEmpty)) return false

    try {
      val uuid = java.util.UUID.fromString(id.get).toString
      val flag = uuid.equals(id.get)
      if(!flag) {
        return flag
      }
      else {
        return getClientById(id.get) != None
      }
    }
    catch {
      case _: Throwable => return false
    }
     getClientById(id.get) != None
  }
}

object Context {
  private var _instanceProd : Context = null
  private var _instanceTest : Context = null
  def instanceProd() = {
    if(_instanceProd == null)
      _instanceProd = new Context()
    _instanceProd
  }
  def instanceTest() = {
    if(_instanceTest == null)
      _instanceTest = new Context()
    _instanceTest
  }
}
