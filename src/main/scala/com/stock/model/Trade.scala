package com.stock.model

case class Trade(
  clientName: String,
  direction: Direction,
  security: String,
  price: Int,
  volume: Int
)

object Trade {
  def apply(o: Order) =
    new Trade(o.clientName, o.direction, o.security, o.price, o.volume)
}