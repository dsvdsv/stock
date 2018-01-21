package com.stock


import java.nio.file.StandardOpenOption

import cats.effect.IO
import fs2.StreamApp.ExitCode
import fs2.{Sink, Stream, StreamApp}

object App extends StreamApp[IO] {


  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] = {
    val accountStream = readFileStream("clients").through(File.accounts)
    val orderStream = readFileStream("orders").through(File.orders)
    val tradeStream = orderStream.through(MatcherFlow.mkTrades)

    for {
      accounts <- Stream.eval(accountStream.compile.toVector)
      updated <- Stream.eval(tradeStream.compile.fold(accounts.toList)(AccountService.updateAccounts))

      _ <- Stream.emits(updated.sortBy(_.clientName)).covary[IO].through(File.encodeAccounts).to(writeFileStream("results"))
      _ <- Stream.eval(requestShutdown)
    } yield ExitCode.Success
  }

  def readFileStream(n: String): Stream[IO, String] =
    fs2.io.file.readAll[IO](new java.io.File(s"src/main/resources/$n.txt").toPath, 512)
      .through(fs2.text.utf8Decode)

  def writeFileStream(n: String): Sink[IO, Byte] =
    fs2.io.file.writeAll[IO](new java.io.File(s"$n.txt").toPath, List(StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))
}
