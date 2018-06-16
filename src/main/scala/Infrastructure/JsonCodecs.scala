package Infrastructure

import java.util.Date

import Expenses.Model.{Expense, TravelExpense}
import io.circe.syntax._
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder, ObjectEncoder}
import squants.market.Money

object JsonCodecs {
  object implicits {
    implicit val dateEncodeJson: Encoder[Date] = Encoder.forProduct1("time") {
      d : Date => d.getTime
    }

    implicit val dateDecodeJson: Decoder[Date] = Decoder.forProduct1("time") {
      time: Long => new Date(time)
    }

    implicit val moneyEncodeJson: Encoder[Money] = Encoder.forProduct2("amount", "currency") {
      m : Money => (m.amount, m.currency.toString())
    }

    implicit val moneyDecodeJson: Decoder[Money] = Decoder.forProduct2("amount", "currency") {
      (amount: BigDecimal, currency: String) => Money(amount, currency)
    }

    implicit val travelExpenseEncodeJson: ObjectEncoder[TravelExpense] = deriveEncoder

    implicit val travelExpenseDecodeJson: Decoder[TravelExpense] = deriveDecoder

    implicit val expenseEncodeJson: Encoder[Expense] = Encoder.instance[Expense] {
      case te @ TravelExpense(_, _, _, _) => te.asJsonObject.add("type", "travel".asJson).asJson
      case _ => throw new UnsupportedOperationException
    }

    implicit val expenseDecodeJson : Decoder[Expense] = for {
      expenseType <- Decoder[String].prepare(_.downField("type"))
      expense <- expenseType match {
        case "travel" => Decoder[TravelExpense]
        case other => Decoder.failedWithMessage(s"invalid type: $other")
      }
    } yield expense

    implicit val expenseListEncodeJson: Encoder[List[Expense]] = Encoder.encodeList[Expense]

    implicit val expenseListDecodeJson: Decoder[List[Expense]] = Decoder.decodeList[Expense]
  }
}
