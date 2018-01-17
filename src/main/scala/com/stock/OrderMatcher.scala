package com.stock

import com.stock.model.{Order, Trade}
import fs2.{Pipe, Pull}

object OrderMatcher {

  def mkTrade[F[_]]: Pipe[F, Order, Trade] = {
    _.pull.unconsChunk.flatMap{
      case None => Pull.pure(None)
      case Some((chunk, s)) => Pull.pure(None)
    }.stream
  }

}
