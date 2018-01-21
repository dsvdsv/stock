package com.stock

import com.stock.model.{Account, Buy, Sell, Trade}
import org.scalatest.{FunSpec, Matchers}

class AccountServiceSpec extends FunSpec with Matchers {

  it("sell") {
    val accounts = List(
      Account("c1", 100, Map("A" -> 10, "B" -> 3)),
      Account("c2", 50, Map("A" -> 5, "B" -> 3))
    )

    AccountService.updateAccounts(accounts, Trade("c1", Sell, "A", 10, 100)) should contain only(
      Account("c1", 1100, Map("A" -> -90, "B" -> 3)),
      Account("c2", 50, Map("A" -> 5, "B" -> 3))
    )
  }

  it("buy") {
    val accounts = List(
      Account("c1", 100, Map("A" -> 10, "B" -> 3)),
      Account("c2", 50, Map("A" -> 5, "B" -> 3))
    )

    AccountService.updateAccounts(accounts, Trade("c1", Buy, "A", 10, 100)) should contain only(
      Account("c1", -900, Map("A" -> 110, "B" -> 3)),
      Account("c2", 50, Map("A" -> 5, "B" -> 3))
    )
  }

}
