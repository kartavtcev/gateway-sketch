package gateway

import gateway.restapi.domain.{TransactionCurrency, TransactionModel, TransactionStatus, WalletModel}
import io.circe.generic.auto._

class GatewayTest extends BaseServiceTest {

  trait Context {
    val cleanUp = cleanContext
    val testClients = provisionClientsList(5)
    val route = httpService.gatewayRouter.route
  }

  "Gateway" should {

    "process transaction" in new Context {
      val testClient = testClients(0)
      val amount: BigDecimal = 250.0
      val url = s"/gateway/process?paymenttype=topup&issuer=${testClient.id.get}&amount=${amount.toString}"

      Post(url) ~> route ~> check {
        val model = responseAs[TransactionModel]
        model.amount should be(amount.toString)
        model.status should be(TransactionStatus.Accepted.toString)
      }
    }

    "return client's wallet with correct balance and default USD currency" in new Context { // aka user story
      val testClientFrom = testClients(0)
      val testClientTo = testClients(1)
      val amountAll: BigDecimal = 250.0
      val amount1: BigDecimal = 100.0
      val amount2: BigDecimal = 50.0

      val urlTopUp = s"/gateway/process?paymenttype=topup&issuer=${testClientFrom.id.get}&amount=${amountAll.toString}"
      Post(urlTopUp) ~> route ~> check {
        responseAs[TransactionModel].amount should be(amountAll.toString)
      }

      val url1 = s"/gateway/process?paymenttype=sale&issuer=${testClientFrom.id.get}&receiver=${testClientTo.id.get}&amount=${amount1.toString}"
      Post(url1) ~> route ~> check {
        responseAs[TransactionModel].amount should be(amount1.toString)
      }

      val url2 = s"/gateway/process?paymenttype=sale&issuer=${testClientFrom.id.get}&receiver=${testClientTo.id.get}&amount=${amount2.toString}"
      Post(url2) ~> route ~> check {
        responseAs[TransactionModel].amount should be(amount2.toString)
      }

      val urlWalletTo = s"/gateway/wallet?clientId=${testClientTo.id.get}"
      Post(urlWalletTo) ~> route ~> check {
        val model = responseAs[WalletModel]
        model.userId should be(testClientTo.id.get)
        model.currency should be (TransactionCurrency.USD.toString)
        model.total should be ((amount1 + amount2).toString)
      }

      val urlWalletFrom = s"/gateway/wallet?clientId=${testClientFrom.id.get}"
      Post(urlWalletFrom) ~> route ~> check {
        val model = responseAs[WalletModel]
        model.userId should be(testClientFrom.id.get)
        model.currency should be (TransactionCurrency.USD.toString)
        model.total should be ((amountAll - (amount1 + amount2)).toString)
      }

    }

    "reject < 0 balance" in new Context {
      val testClientFrom = testClients(0)
      val amount: BigDecimal = 100.0

      val urlTopUp = s"/gateway/process?paymenttype=withdraw&issuer=${testClientFrom.id.get}&amount=${amount.toString}"
      Post(urlTopUp) ~> route ~> check {
        responseAs[TransactionModel].status should be(TransactionStatus.Rejected.toString)
      }
    }
  }
}