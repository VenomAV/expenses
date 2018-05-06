package Expenses.Model

import Expenses.TestUtils.CustomGen
import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}
import scalaz.{Failure, NonEmptyList, Success}

object TravelExpenseSpec extends Properties("TravelExpense") {
  property("date should be less or equal to today") =
    forAll(CustomGen.strictlyPositiveMoney("EUR"),
      CustomGen.calendarInTheFuture,
      Gen.alphaNumStr,
      Gen.alphaNumStr) {
      (cost, date, from, to) => TravelExpense.create(cost, date.getTime, from, to) match {
        case Failure(NonEmptyList(head, _)) if "date cannot be in the future".equals(head) => true
        case _ => false
      }
    }

  property("to should not be null or empty") =
    forAll(CustomGen.strictlyPositiveMoney("EUR"),
      CustomGen.calendarInThePast,
      CustomGen.notNullOrEmptyString,
      CustomGen.nullOrEmptyString) {
      (cost, date, from, to) => TravelExpense.create(cost, date.getTime, from, to) match {
        case Failure(NonEmptyList(head, _)) if "to is null or empty".equals(head) => true
        case _ => false
      }
    }

  property("from should not be null or empty") =
    forAll(CustomGen.strictlyPositiveMoney("EUR"),
      CustomGen.calendarInThePast,
      CustomGen.nullOrEmptyString,
      CustomGen.notNullOrEmptyString) {
      (cost, date, from, to) => TravelExpense.create(cost, date.getTime, from, to) match {
        case Failure(NonEmptyList(head, _)) if "from is null or empty".equals(head) => true
        case _ => false
      }
    }

  property("cost should not be zero or negative") =
    forAll(Gen.oneOf(CustomGen.negativeMoney("EUR"), CustomGen.zeroMoney("EUR")),
      CustomGen.calendarInThePast,
      CustomGen.notNullOrEmptyString,
      CustomGen.notNullOrEmptyString) {
      (cost, date, from, to) => TravelExpense.create(cost, date.getTime, from, to) match {
        case Failure(NonEmptyList(head, _)) if "cost is less or equal to zero".equals(head) => true
        case _ => false
      }
    }

  property("when success should contain the given values") =
    forAll(CustomGen.strictlyPositiveMoney("EUR"),
      CustomGen.calendarInThePast,
      CustomGen.notNullOrEmptyString,
      CustomGen.notNullOrEmptyString) {
      (cost, date, from, to) => TravelExpense.create(cost, date.getTime, from, to) match {
        case Success(TravelExpense(c, d, f, t)) =>
          c == cost && d == date.getTime && f == from && t == to
        case _ => false
      }
    }
}
