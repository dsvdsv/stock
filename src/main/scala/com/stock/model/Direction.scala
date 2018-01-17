package com.stock.model

sealed trait Direction

case object Buy extends Direction
case object Sell extends Direction
