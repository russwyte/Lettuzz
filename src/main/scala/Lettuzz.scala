package rocks.effect.early.lettuzz

import io.lettuce.core.*

import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.async.RedisAsyncCommands
import io.lettuce.core.cluster.RedisClusterClient
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection
import io.lettuce.core.cluster.api.async.RedisClusterAsyncCommands
import io.lettuce.core.codec.{RedisCodec, StringCodec}
import zio.*

import java.security.cert.CertPathValidatorException.Reason
import scala.language.implicitConversions

def makeZIO[T](f: RedisFuture[T]): Task[T] =
  ZIO.async { callback =>
    f.thenAccept(value => callback(ZIO.attempt(value)))
  }

extension [T](f: RedisFuture[T]) def toZIO: Task[T] = makeZIO(f)

given Conversion[RedisFuture[_], Task[_]] with
  def apply(f: RedisFuture[_]): Task[_] = makeZIO(f)

type CloseableResources = RedisClient | RedisClusterClient | StatefulRedisConnection[_, _] |
  StatefulRedisClusterConnection[_, _]

def close(c: CloseableResources) =
  for
    _ <- Console.printLine(s"closing: ${c.getClass.getSimpleName}").orDie
    _ <- ZIO.succeed(c.close())
  yield ()

def client(client: => RedisClient) =
  ZLayer.scoped { ZIO.attempt(client).withFinalizer(close) }

def commands[K, V](codec: RedisCodec[K, V] = StringCodec.UTF8)(using Tag[K], Tag[V]) =
  ZLayer.scoped {
    for
      c   <- ZIO.service[Connector]
      res <- c.connect(codec)
    yield res
  }
