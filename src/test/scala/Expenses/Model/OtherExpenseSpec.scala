package Expenses.Model

import Expenses.TestUtils.CustomGen
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties
import scalaz.{Failure, NonEmptyList, Success}

object OtherExpenseSpec extends Properties("OtherExpense") {
  property("description should contain more than 9 words") =
    forAll(CustomGen.strictlyPositiveMoney("EUR"),
      CustomGen.calendarInThePast,
      CustomGen.sentence(maxWordCount = 9)) {
      (cost, date, description) => OtherExpense.create(cost, date.getTime, description) match {
        case Failure(NonEmptyList(head, _)) if "description contains less than 10 words".equals(head) => true
        case _ => false
      }
    }

  property("when success should contain the given values") =
    forAll(CustomGen.strictlyPositiveMoney("EUR"),
      CustomGen.calendarInThePast,
      CustomGen.sentence(10, 100)) {
      (cost, date, description) => OtherExpense.create(cost, date.getTime, description) match {
        case Success(OtherExpense(c, d, x)) => c == cost && d == date.getTime && x == description
        case _ => false
      }
    }
}
