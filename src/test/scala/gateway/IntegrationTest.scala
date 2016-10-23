package gateway

import akka.http.scaladsl.model.StatusCodes._
import gateway.restapi.domain.{TransactionCurrency, TransactionModel, WalletModel}
import io.circe.generic.auto._

class IntegrationTest extends BaseServiceTest {

  trait Context {
    val cleanUp = cleanContext
    val testClients = provisionClientsList(5)
    val route = httpService.routes
  }

  "Gateway Integrated System" should {

    "have consistent memory storage after custom gateway exception thrown" in new Context {
      val client = testClients(0)
      val amount1: BigDecimal = 50.0
      val amount2: BigDecimal = 100.0
      val amount3: BigDecimal = 10.0

      val urlTopUp = s"/v1/gateway/process?paymenttype=topup&issuer=${client.id.get}&amount=${amount1.toString}"
      Post(urlTopUp) ~> route ~> check {
        responseAs[TransactionModel].amount should be(amount1.toString)
      }

      val urlWithdrawFail = s"/v1/gateway/process?paymenttype=withdraw&issuer=invalidId&amount=${amount2.toString}"
      Post(urlWithdrawFail) ~> route ~> check {
        status shouldBe BadRequest
      }

      val urlWithdrawSuccess = s"/v1/gateway/process?paymenttype=withdraw&issuer=${client.id.get}&amount=${amount3.toString}"
      Post(urlWithdrawSuccess) ~> route ~> check {
        responseAs[TransactionModel].amount should be((amount3).toString)
      }

      val urlWallet = s"/v1/gateway/wallet?clientId=${client.id.get}"
      Post(urlWallet) ~> route ~> check {
        val model = responseAs[WalletModel]
        model.userId should be(client.id.get)
        model.currency should be(TransactionCurrency.USD.toString)
        model.total should be((amount1 - amount3).toString)
      }
    }
  }
}
