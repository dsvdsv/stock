package com.stock.model

case class Trade(
  clientName: String,
  direction: Direction,
  security: String,
  price: Int,
  volume: Int
)