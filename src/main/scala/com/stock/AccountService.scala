package com.stock

import com.stock.model.{Account, Buy, Sell, Trade}

object AccountService {
  def updateAccounts(accounts: List[Account], trade: Trade): List[Account] = {
    accounts.find(_.clientName == trade.clientName) match {
      case None          => accounts
      case Some(account) =>
        val sb = account.portfolio(trade.security)

        val updatedAccount = trade.direction match {
          case Sell =>
            val toAmount = trade.volume * trade.price
            account.copy(
              amount = account.amount + toAmount,
              portfolio = account.portfolio + (trade.security -> (sb - trade.volume))
            )
          case Buy  =>
            val toAmount = trade.volume * trade.price
            account.copy(
              amount = account.amount - toAmount,
              portfolio = account.portfolio + (trade.security -> (sb + trade.volume))
            )
        }

        accounts.filterNot(_.clientName == account.clientName) :+ updatedAccount
    }

  }
}
