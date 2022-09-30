package rocks.effect.early.lettuzz

import zio.test.*
import zio.*
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName
import org.testcontainers.containers.DockerComposeContainer
import io.lettuce.core.api.async.*
import io.lettuce.core.codec.StringCodec
import io.lettuce.core.RedisClient
import com.github.dockerjava.api.model.Service

class RedisContainer:
  val container: GenericContainer[_] =
    GenericContainer(DockerImageName.parse("redis:5.0.3-alpine")).withExposedPorts(6379)
  container.start()
  def stop = ZIO.attempt(container.stop()).orDie
  def host = container.getHost()
  def port = container.getFirstMappedPort()
end RedisContainer

object RedisContainer:
  val layer =
    ZLayer.scoped(ZIO.attempt(RedisContainer()).withFinalizer(_.stop))

object Client:
  val layer = ZLayer.scoped {
    for
      c   <- ZIO.service[RedisContainer]
      res <- ZIO.attempt(RedisClient.create(s"redis://${c.host}:${c.port}")).withFinalizer(close)
    yield res

  }

object BasicSpec extends ZIOSpecDefault:
  def spec =
    suite("basic stuff should work") {
      test("like reading what you wrote") {
        for
          _ <- ZIO.serviceWithZIO[BizLogic] { _.run }
          _ <- Console.printLine("done")
        yield assertTrue(true)
      }
    }.provide(
      RedisContainer.layer,
      BizLogic.layer,
      commands(StringCodec.UTF8),
      Connector.layer,
      Client.layer,
    )
end BasicSpec

case class BizLogic(redis: RedisAsyncCommands[String, String]):
  val run =
    for
      now    <- Clock.nanoTime.map(_.toString())
      set    <- redis.set("foo", now).toZIO.fork
      setRes <- set.join
      getRes <- redis.get("foo").toZIO
      _      <- Console.printLine(setRes, getRes)
    yield assertTrue(getRes == now)
end BizLogic

object BizLogic:
  val layer = ZLayer.scoped {
    for
      redis <- ZIO.service[RedisAsyncCommands[String, String]]
      res   <- ZIO.succeed(BizLogic(redis))
    yield res
  }
