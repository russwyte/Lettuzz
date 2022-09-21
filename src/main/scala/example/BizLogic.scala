package rocks.effect.early.lettuzz
package example

import io.lettuce.core.api.async.RedisAsyncCommands
import zio.*

import scala.language.implicitConversions

case class BizLogic(redis: RedisAsyncCommands[String, String]):
  val run =
    for
      now    <- Clock.nanoTime
      set    <- redis.set("foo", now.toString).fork
      setRes <- set.join
      getRes <- redis.get("foo")
      _      <- Console.printLine(setRes, getRes)
    yield ()
end BizLogic

object BizLogic:
  val layer = ZLayer.scoped {
    for
      redis <- ZIO.service[RedisAsyncCommands[String, String]]
      res   <- ZIO.succeed(BizLogic(redis))
    yield res
  }
