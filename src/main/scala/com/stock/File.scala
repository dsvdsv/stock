package com.stock


import com.stock.model.{Account, Buy, Order, Sell}
import fs2.Pipe

object File {

  def encodeAccount(a: Account) =
    s"${a.clientName}\t${a.amount}\t${a.portfolio("A")}\t${a.portfolio("B")}\t${a.portfolio("C")}\t${a.portfolio("D")}"

  def accounts[F[_]]: Pipe[F, String, Account] =
    _.through(decodedElements(unsafeDecodeAccount))

  def unsafeDecodeAccount(str: String) = str.split("\t") match {
    case Array(c, a, aa, ba, ca, da) =>
      Account(
        clientName = c,
        amount = a.toInt,
        portfolio = Map("A" -> aa.toInt, "B" -> ba.toInt, "C" -> ca.toInt, "D" -> da.toInt)
      )
    case _ =>
      throw new IllegalArgumentException(s"Invalid account: $str")
  }

  def decodedElements[F[_], V](f: String => V): Pipe[F, String, V] =
    _.through(fs2.text.lines)
      .filter(!_.isEmpty)
      .map(f)

  def orders[F[_]]: Pipe[F, String, Order] =
    _.through(decodedElements(unsafeDecodeOrder))

  def unsafeDecodeOrder(str: String) = str.split("\t") match {
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

  def unsafeParseDirection(s: String) = s match {
    case "s" => Sell
    case "b" => Buy
    case _ => throw new IllegalArgumentException(s"Invalid direction: $s")
  }

  def encodeAccounts[F[_]]: Pipe[F, Account, Byte] =
    _.map(encodeAccount).intersperse("\n").through(fs2.text.utf8Encode)

}
