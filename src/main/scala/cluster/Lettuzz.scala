package rocks.effect.early.lettuzz
package cluster

import io.lettuce.core.cluster.RedisClusterClient
import io.lettuce.core.codec.{RedisCodec, StringCodec}
import zio.{Tag, ZIO, ZLayer}

def client(client: => RedisClusterClient) =
  ZLayer.scoped { ZIO.attempt(client).withFinalizer(close) }

def commands[K, V](codec: RedisCodec[K, V] = StringCodec.UTF8)(using Tag[K], Tag[V]) =
  ZLayer.scoped {
    for
      c   <- ZIO.service[ClusterConnector]
      res <- c.connect(codec)
    yield res
  }
