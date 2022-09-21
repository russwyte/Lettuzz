package rocks.effect.early.lettuzz

import io.lettuce.core.cluster.RedisClusterClient
import io.lettuce.core.cluster.api.async.RedisClusterAsyncCommands
import io.lettuce.core.codec.{RedisCodec, StringCodec}
import zio.*

case class ClusterConnector(client: RedisClusterClient):
  def connect[K, V](codec: RedisCodec[K, V] = StringCodec.UTF8)(using Tag[K], Tag[V]): ZIO[
    Scope,
    Throwable,
    RedisClusterAsyncCommands[K, V],
  ] =
    ZIO.attempt(client.connect(codec)).withFinalizer(close).map(_.async())

object ClusterConnector:
  val layer = ZLayer.scoped {
    for
      c   <- ZIO.service[RedisClusterClient]
      res <- ZIO.succeed(ClusterConnector(c))
    yield res
  }
