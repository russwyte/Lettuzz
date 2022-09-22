package rocks.effect.early.lettuzz

import io.lettuce.core.RedisClient
import io.lettuce.core.api.async.RedisAsyncCommands
import io.lettuce.core.cluster.RedisClusterClient
import io.lettuce.core.cluster.api.async.RedisClusterAsyncCommands
import io.lettuce.core.codec.{RedisCodec, StringCodec}
import zio.*

case class Connector(client: RedisClient):
  def connect[K, V](codec: RedisCodec[K, V] = StringCodec.UTF8)(using Tag[K], Tag[V]): ZIO[
    Scope,
    Throwable,
    RedisAsyncCommands[K, V],
  ] =
    ZIO.attempt(client.connect(codec)).withFinalizer(close).map(_.async())

object Connector:
  val layer = ZLayer.scoped {
    for
      c   <- ZIO.service[RedisClient]
      res <- ZIO.succeed(Connector(c))
    yield res
  }
