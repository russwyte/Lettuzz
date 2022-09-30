package rocks.effect.early.lettuzz
package example

import io.lettuce.core.RedisClient
import io.lettuce.core.codec.StringCodec
import zio.*

object App extends ZIOAppDefault:
  assert(true)
  val run = ZIO
    .serviceWithZIO[BizLogic](_.run)
    .provide(
      BizLogic.layer,
      commands(StringCodec.UTF8),
      Connector.layer,
      client(RedisClient.create("redis://localhost")),
    )
end App
