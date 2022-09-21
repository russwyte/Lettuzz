package rocks.effect.early.lettuzz
package example

import dummy.dummy

object App extends ZIOAppDefault:
  val run = ZIO
    .serviceWithZIO[BizLogic](_.run)
    .provide(
      BizLogic.layer,
      commands(StringCodec.UTF8),
      Connector.layer,
      client(RedisClient.create("redis://localhost")),
    )
end App
