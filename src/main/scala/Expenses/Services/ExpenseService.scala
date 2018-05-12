package Expenses.Services

import Expenses.Model._

trait ExpenseService[F[_]] {
  def openFor(employee: Employee): F[OpenExpenseSheet]
  def addExpenseTo(expense: Expense, expenseSheet: OpenExpenseSheet): F[OpenExpenseSheet]
  def claim(expenseSheet: OpenExpenseSheet): F[(ClaimedExpenseSheet, PendingClaim)]
}

