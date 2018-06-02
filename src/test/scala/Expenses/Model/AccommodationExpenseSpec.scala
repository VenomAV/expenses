package Expenses.Model

import Expenses.TestUtils.CustomGen
import cats.data.NonEmptyList
import cats.data.Validated.{Invalid, Valid}
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties

object AccommodationExpenseSpec extends Properties("AccommodationExpense") {
  property("hotel should not be empty") =
    forAll(CustomGen.strictlyPositiveMoney("EUR"),
      CustomGen.calendarInThePast,
      CustomGen.nullOrEmptyString) {
      (cost, date, hotel) => Expense.createAccommodation(cost, date.getTime, hotel) match {
        case Invalid(NonEmptyList(head, _)) if "hotel is null or empty".equals(head) => true
        case _ => false
      }
    }

  property("when success should contain the given values") =
    forAll(CustomGen.strictlyPositiveMoney("EUR"),
      CustomGen.calendarInThePast,
      CustomGen.notNullOrEmptyString) {
      (cost, date, hotel) => Expense.createAccommodation(cost, date.getTime, hotel) match {
        case Valid(AccommodationExpense(c, d, h)) => c == cost && d == date.getTime && h == hotel
        case _ => false
      }
    }
}


