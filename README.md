Gateway Debit sketch
=========================

## Sample REST API 
* TopUp, Withdraw, Sale, Refund transactions
* Accepted, Pending, Rejected, Pending transaction statuses
* multi-national currencies in separate wallets by currency, by client
* test coverage with *ScalaTest*

## Dependencies
* JDK 8
* sbt
* scala 2.11.8
* akka.http 2.4.10
* scalatest 3.0.0

## REST API (check tests for sample routes):
Version Prefix: /v1/
```
/clients
    GET <- retrieve clients list
    POST <- create a new client (or replace existing. no Update/Delete operation in demo)
    
/clients/"clientGuidId"
    GET <- retrieve client by id
```    

``` 
/gateway/process
    POST <- paymentType, issuer[Option], receiver[Option], currency([Option], DEFAULT is "USD"), amount(check >= 0)
    (check that Client's total > transaction amount to process it, if withdraw / sale)
/gateway/wallet
    POST <- clientId, currency ([Option], DEFAULT is "USD")
``` 

```
Sample:

http://localhost:9000/v1/clients/ <- POST

Request: { "name": "Smith Test" }. Content-Type application/json

Respone:
{
  "id": "45f535d0-7efb-4471-ba82-c61f5e0a4cd6",
  "name": "Smith Test",
  "isActive": true
}
``` 

## Not implemented:
* no auth security
* no transactions rollback in terms of business transactions
* no persistent storage - (like in memory postgres)
* no proper error handling: now is NoContent for all tech + business exceptions (GateWay typed exception) + technical error message
* no Futures - only async java collection wrapped to scala
* no custom actors declared in code
* no docker
* no proper tests coverage - just a few sample unit tests (all green)
* not much "toxic" Monads, only basics like [Option]