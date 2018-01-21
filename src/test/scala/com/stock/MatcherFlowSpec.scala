package com.stock

import cats.effect.IO
import com.stock.model.{Buy, Order, Sell, Trade}
import fs2.Stream
import org.scalatest.{FunSpec, Matchers}

class MatcherFlowSpec extends FunSpec with Matchers {

  it("opposite orders are exist") {
    val orders = Seq(
      Order("c1", Sell, "a", 1, 1),
      Order("c1", Sell, "a", 2, 1),
      Order("c2", Buy, "a", 2, 1)
    )

    flow(orders) should contain only(
      Trade("c1", Sell, "a", 2, 1),
      Trade("c2", Buy, "a", 2, 1)
    )
  }

  it("opposite order aren't exist") {
    val orders = Seq(
      Order("c1", Sell, "a", 1, 1),
      Order("c3", Sell, "b", 2, 1),
      Order("c2", Buy, "c", 2, 1),
      Order("c4", Buy, "d", 2, 1)
    )

    flow(orders) should be(empty)
  }

  it("same orders should be swap") {
    val orders = Seq(
      Order("c1", Sell, "a", 1, 1),
      Order("c1", Sell, "a", 2, 1),
      Order("c2", Buy, "a", 2, 1),
      Order("c2", Buy, "a", 1, 1)
    )

    flow(orders) should contain only(
      Trade("c1", Sell, "a", 2, 1),
      Trade("c2", Buy, "a", 2, 1)
    )
  }

  it("fifo") {
    val orders = Seq(
      Order("c1", Sell, "a", 2, 1),
      Order("c2", Sell, "a", 2, 1),
      Order("c3", Buy, "a", 2, 1)
    )

    flow(orders) should contain only(
      Trade("c1", Sell, "a", 2, 1),
      Trade("c3", Buy, "a", 2, 1)
    )
  }

  def flow(orders: Seq[Order]): Seq[Trade] =
    Stream.emits(orders)
      .covary[IO]
      .through(MatcherFlow.mkTrades)
      .compile
      .toVector
      .unsafeRunSync
}

