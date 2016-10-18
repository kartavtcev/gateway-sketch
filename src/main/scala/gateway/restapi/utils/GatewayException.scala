package gateway.restapi.utils

case class GatewayException(message: String = "", cause: Throwable = null) extends Exception(message, cause)

object PredefGateway {
  @inline final def require(requirement: Boolean, message: => Any) {
    if (!requirement)
      throw new GatewayException("requirement failed: "+ message)
  }
}