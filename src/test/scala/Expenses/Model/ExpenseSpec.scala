package Expenses.Model

import java.util.Calendar

import org.scalacheck.{Gen, Properties}
import org.scalacheck.Prop.forAll
import scalaz.{Failure, NonEmptyList}
import squants.market.Money

object ExpenseSpec extends Properties("Expense date should always be less or equal to today") {
  property("travelExpense with date in the future returns error") =
    forAll(Gen.calendar suchThat(_.after(Calendar.getInstance()))) {
      date => Expense.travelExpense(Money.apply(1, "EUR"), date.getTime(), "Florence", "Milan") match {
        case Failure(NonEmptyList(head, _)) if "date cannot be in the future".equals(head) => true
        case _ => false
      }
    }
}
