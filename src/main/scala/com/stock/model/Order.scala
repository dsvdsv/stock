package com.stock.model

case class Order(
  clientName: String,
  direction: Direction,
  security: String,
  price: Int,
  volume: Int
)