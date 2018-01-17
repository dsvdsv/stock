package com.stock.model

case class Account(
  clientName: String,
  amount: Int,
  portfolio: Map[String, Int]
)

