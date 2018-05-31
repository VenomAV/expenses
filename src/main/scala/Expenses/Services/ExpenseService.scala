package Expenses.Services

import Expenses.Model._
import Expenses.Utils.Validation
import Expenses.Utils.Validation.Result
import cats.Id
import cats.data.NonEmptyList
import cats.implicits._

trait ExpenseService[F[_]] {
  def openFor(employee: Employee): F[Validation.Result[OpenExpenseSheet]]
  def addExpenseTo(expense: Expense, expenseSheet: OpenExpenseSheet): F[Validation.Result[OpenExpenseSheet]]
  def claim(expenseSheet: OpenExpenseSheet): F[Validation.Result[(ClaimedExpenseSheet, PendingClaim)]]
}

class IdExpenseService extends ExpenseService[Id] {
  override def openFor(employee: Employee): Id[Result[OpenExpenseSheet]] =
    OpenExpenseSheet.create(employee, List[Expense]())

  override def addExpenseTo(expense: Expense, expenseSheet: OpenExpenseSheet): Id[Result[OpenExpenseSheet]] =
    OpenExpenseSheet.create(expenseSheet.id, expenseSheet.employee, expenseSheet.expenses :+ expense)

  override def claim(expenseSheet: OpenExpenseSheet): Id[Result[(ClaimedExpenseSheet, PendingClaim)]] =
    expenseSheet.expenses match {
      case h::t =>
        (ClaimedExpenseSheet.create(expenseSheet.id, expenseSheet.employee, expenseSheet.expenses),
          PendingClaim.create(expenseSheet.employee, NonEmptyList(h, t))).mapN((_, _))
      case _ => "Cannot claim empty expense sheet".invalidNel
    }
}

