package com.stock

import com.stock.model.{Order, Trade}
import fs2.{Pipe, Pull, Segment, Stream}

object MatcherFlow {

  def mkTrades[F[_]]: Pipe[F, Order, Trade] = {
    def matchOrder(o1: Order): PartialFunction[Order, Order] = {
      case o2@Order(_, d, o1.security, o1.price, o1.volume) if d != o1.direction => o2
    }

    def isSameOrder(o1: Order, o2: Order): Boolean =
      o1.direction == o2.direction && o1.clientName == o2.clientName && o1.security == o2.security

    def go(orderBook: List[Order], s: Stream[F, Order]): Pull[F, Trade, Unit] = {
      s.pull.uncons1.flatMap {
        case None => Pull.done
        case Some((o1, tl)) =>
          orderBook.collectFirst(matchOrder(o1)) match {
            case None =>
              val ob = orderBook
                .filterNot(isSameOrder(_, o1))
              go(ob :+ o1, tl)
            case Some(o2) =>
              val t1 = Trade(o1)
              val t2 = Trade(o2)
              Pull.output(Segment(t1, t2)) >> go(orderBook.filter(_ == o2), tl)
          }
      }
    }

    s => go(List.empty, s).stream
  }

}