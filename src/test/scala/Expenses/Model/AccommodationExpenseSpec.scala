package Expenses.Model

import Expenses.TestUtils.CustomGen
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties
import scalaz.{Failure, NonEmptyList, Success}

object AccommodationExpenseSpec extends Properties("AccommodationExpense") {
  property("hotel should not be empty") =
    forAll(CustomGen.strictlyPositiveMoney("EUR"),
      CustomGen.calendarInThePast,
      CustomGen.nullOrEmptyString) {
      (cost, date, hotel) => AccommodationExpense.create(cost, date.getTime, hotel) match {
        case Failure(NonEmptyList(head, _)) if "hotel is null or empty".equals(head) => true
        case _ => false
      }
    }

  property("when success should contain the given values") =
    forAll(CustomGen.strictlyPositiveMoney("EUR"),
      CustomGen.calendarInThePast,
      CustomGen.notNullOrEmptyString) {
      (cost, date, hotel) => AccommodationExpense.create(cost, date.getTime, hotel) match {
        case Success(AccommodationExpense(c, d, h)) => c == cost && d == date.getTime && h == hotel
        case _ => false
      }
    }
}


