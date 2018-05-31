package Expenses.Services

import Expenses.Model.OpenExpenseSheet
import Expenses.TestUtils.CustomGen
import cats.Applicative
import cats.data.Validated.Valid
import cats.implicits._
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties

object ExpenseServiceSpec extends Properties("ExpenseServiceSpec") {
  property("should use given employee to create an open sheet") = forAll(CustomGen.employee) {
    employee =>
      ExpenseService.openFor(employee) match {
        case Valid(sheet) => sheet.employee == employee
        case _ => false
      }
  }

  property("should add expense to those of the given expense sheet") =
    forAll(CustomGen.openExpenseSheet, CustomGen.expense) {
      (openExpenseSheet, expense) =>
        ExpenseService.addExpenseTo(expense, openExpenseSheet) match {
          case Valid(OpenExpenseSheet(id, employee, expenses)) => id == openExpenseSheet.id &&
            employee == openExpenseSheet.employee && expenses.contains(expense)
          case _ => false
        }
    }
}
