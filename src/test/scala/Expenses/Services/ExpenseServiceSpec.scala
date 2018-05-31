package Expenses.Services

import Expenses.Model.OpenExpenseSheet
import Expenses.Services.ExpenseServiceSpec.ExpenseServiceLaws
import Expenses.TestUtils.CustomGen
import cats.Applicative
import cats.data.Validated.Valid
import cats.implicits._
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties

object ExpenseServiceSpec {
  class ExpenseServiceLaws[F[_]: Applicative](service: ExpenseService[F], name: String) extends Properties(name) {
    property("should use given employee to create an open sheet") = forAll(CustomGen.employee) {
      employee => service.openFor(employee).map( {
        case Valid(sheet) => sheet.employee == employee
        case _ => false
      }) == Applicative[F].pure(true)
    }

    property("should add expense to those of the given expense sheet") =
      forAll(CustomGen.openExpenseSheet, CustomGen.expense) {
        (openExpenseSheet, expense) => service.addExpenseTo(expense, openExpenseSheet)
          .map({
            case Valid(OpenExpenseSheet(id, employee, expenses)) => id == openExpenseSheet.id &&
              employee == openExpenseSheet.employee && expenses.contains(expense)
            case _ => false
          }) == Applicative[F].pure(true)
      }
  }
}

object ImplementationExpenseServiceSpec extends  ExpenseServiceLaws(
  new IdExpenseService, "ImplementationExpenseServiceSpec")
