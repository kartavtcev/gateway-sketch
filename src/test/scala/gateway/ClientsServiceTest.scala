package gateway

import akka.http.scaladsl.model.{HttpEntity, MediaTypes}
import gateway.restapi.domain.ClientEnitity
import io.circe.generic.auto._
import org.scalatest.concurrent.ScalaFutures

class ClientsServiceTest extends BaseServiceTest with ScalaFutures {

  trait Context {
    val cleanUp = cleanContext
    val testClients = provisionClientsList(5)
    val route = httpService.clientsRouter.route
  }

  "Users service" should {

    "retrieve empty clients list" in new Context {
      cleanContext
      Get("/clients") ~> route ~> check {
        responseAs[Seq[ClientEnitity]].length should be(0)
      }
    }

    "retrieve clients list" in new Context {
      Get("/clients") ~> route ~> check {
        responseAs[Seq[ClientEnitity]].length should be(5)
      }
    }

    "retrieve client by id" in new Context {
      val testClient = testClients(4)
      Get(s"/clients/${testClient.id.get}") ~> route ~> check {
        responseAs[ClientEnitity] should be(testClient)
      }
    }

    "create a new client" in new Context {
      val newClientName = "Smith Test"
      val requestEntity = HttpEntity(MediaTypes.`application/json`, s"""{"name": "$newClientName"}""")
      Post("/clients/", requestEntity) ~> route ~> check {
        responseAs[ClientEnitity].name should be(newClientName)
      }
    }
  }
}
