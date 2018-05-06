package Expenses.Model

import Expenses.TestUtils.CustomGen
import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}
import scalaz.{Failure, NonEmptyList, Success}

object FoodExpenseSpec extends Properties("FoodExpense") {
  property("cost should be less than 50") =
    forAll(CustomGen.money(Gen.chooseNum[Double](50, Double.MaxValue), "EUR"),
      CustomGen.calendarInThePast) {
      (cost, date) => FoodExpense.create(cost, date.getTime) match {
        case Failure(NonEmptyList(head, _)) if "cost is greater than or equal to 50".equals(head) => true
        case _ => false
      }
    }

  property("when success should contain the given values") =
    forAll(CustomGen.money(Gen.chooseNum[Double](Double.MinPositiveValue, 49.99), "EUR"),
      CustomGen.calendarInThePast) {
      (cost, date) => FoodExpense.create(cost, date.getTime) match {
        case Success(FoodExpense(c, d)) => c == cost && d == date.getTime
        case _ => false
      }
    }
}


