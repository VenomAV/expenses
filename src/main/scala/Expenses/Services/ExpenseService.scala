package Expenses.Services

/*

 */
trait ExpenseService[Employee, Expense, OpenExpenseSheet, ClaimedExpenseSheet, PendingClaim, F[_]] {
  def openFor(employee: Employee): F[OpenExpenseSheet]
  def addExpenseTo(expense: Expense, expenseSheet: OpenExpenseSheet): F[OpenExpenseSheet]
  def claim(expenseSheet: OpenExpenseSheet): F[(ClaimedExpenseSheet, PendingClaim)]
}

