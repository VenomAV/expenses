package Infrastructure.Repositories

import doobie.util.meta.Meta
import io.circe.{Decoder, Encoder, Json}
import io.circe.jawn.parse
import io.circe.syntax._
import org.postgresql.util.PGobject
import cats.implicits._

import scala.reflect.runtime.universe.TypeTag

object Doobie {
  implicit val JsonMeta: Meta[Json] =
    Meta.other[PGobject]("json").xmap[Json](
      a => parse(a.getValue).leftMap[Json](e => throw e).merge,
      a => {
        val o = new PGobject
        o.setType("json")
        o.setValue(a.noSpaces)
        o
      }
    )

  def codecMeta[A: Encoder : Decoder : TypeTag]: Meta[A] =
    Meta[Json].xmap[A](
      _.as[A].fold[A](throw _, identity),
      _.asJson
    )
}
