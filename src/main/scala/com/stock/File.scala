package com.stock


import com.stock.model.{Account, Buy, Order, Sell}
import fs2.Pipe

object File {

  def unsafeParseDirection(s: String) = s match {
    case "s" => Sell
    case "b" => Buy
    case _   => throw new IllegalArgumentException(s"Invalid direction: $s")
  }

  def unsafeParseOrder(str: String) = str.split("\t") match {
    case Array(c, d, s, p, v) =>
      Order(
        clientName = c,
        direction = unsafeParseDirection(d),
        security = s,
        price = p.toInt,
        volume = v.toInt
      )
    case _ =>
      throw new IllegalArgumentException(s"Invalid order: $str")
  }

  def unsafeParseAccount(str: String) = str.split("\t") match {
    case Array(c, a, aa, ba, ca, da) =>
      Account(
        clientName = c,
        amount = a.toInt,
        portfolio = Map("A" -> aa.toInt, "B" -> ba.toInt, "C" -> ca.toInt, "D" -> da.toInt)
      )
    case _ =>
      throw new IllegalArgumentException(s"Invalid account: $str")
  }

  def parsedElements[F[_], V](f: String => V): Pipe[F, String, V] =
    _.through(fs2.text.lines)
      .map(f)

  def accounts[F[_]]: Pipe[F, String, Account] =
    _.through(parsedElements(unsafeParseAccount))

  def orders[F[_]]: Pipe[F, String, Order] =
    _.through(parsedElements(unsafeParseOrder))
}
