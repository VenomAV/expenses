package Expenses.Model

sealed trait ExpenseSheet {
  def employee: Employee
  def expenses:List[Expense]
}

case class OpenExpenseSheet(employee: Employee, expenses:List[Expense]) extends ExpenseSheet
case class ClaimedExpenseSheet(employee: Employee, expenses:List[Expense]) extends ExpenseSheet