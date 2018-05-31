package Expenses.Services

import Expenses.Model._
import Expenses.Utils.Validation
import Expenses.Utils.Validation.Result
import cats.Id
import cats.data.NonEmptyList
import cats.implicits._

object ExpenseService {
  def openFor(employee: Employee): Validation.Result[OpenExpenseSheet] =
    OpenExpenseSheet.create(employee, List[Expense]())

  def addExpenseTo(expense: Expense, expenseSheet: OpenExpenseSheet): Validation.Result[OpenExpenseSheet] =
    OpenExpenseSheet.create(expenseSheet.id, expenseSheet.employee, expenseSheet.expenses :+ expense)

  def claim(expenseSheet: OpenExpenseSheet): Validation.Result[(ClaimedExpenseSheet, PendingClaim)] =
    expenseSheet.expenses match {
      case h::t =>
        (ClaimedExpenseSheet.create(expenseSheet.id, expenseSheet.employee, expenseSheet.expenses),
          PendingClaim.create(expenseSheet.employee, NonEmptyList(h, t))).mapN((_, _))
      case _ => "Cannot claim empty expense sheet".invalidNel
    }
}


