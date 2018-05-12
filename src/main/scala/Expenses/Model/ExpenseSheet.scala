package Expenses.Model

import java.util.UUID

import Expenses.Model.ExpenseSheet.ExpenseSheetId
import Expenses.Utils.Validation
import cats.implicits._

sealed trait ExpenseSheet {
  def id: ExpenseSheetId
  def employee: Employee
  def expenses: List[Expense]
}

case class OpenExpenseSheet private (id: ExpenseSheetId,
                                     employee: Employee,
                                     expenses:List[Expense]) extends ExpenseSheet

case class ClaimedExpenseSheet private (id: ExpenseSheetId,
                                        employee: Employee,
                                        expenses:List[Expense]) extends ExpenseSheet

object ExpenseSheet {
  type ExpenseSheetId = UUID

  val validateId = Validation.notNull[ExpenseSheetId]("id is null")(_)
  val validateEmployee = Validation.notNull[Employee]("employee is null")(_)
}

object OpenExpenseSheet {
  def create(id: ExpenseSheetId, employee: Employee, expenses:List[Expense]) : Validation.Result[OpenExpenseSheet] =
    (ExpenseSheet.validateId(id),
      ExpenseSheet.validateEmployee(employee)).mapN(new OpenExpenseSheet(_, _, expenses))

  def create(employee: Employee, expenses:List[Expense]) : Validation.Result[OpenExpenseSheet] =
    create(UUID.randomUUID(), employee, expenses)
}

object ClaimedExpenseSheet {
  private val validateExpenses = Validation.nonEmptyList[Expense]("expenses is empty")(_)


  def create(id: ExpenseSheetId, employee: Employee, expenses:List[Expense]) : Validation.Result[ClaimedExpenseSheet] =
    (ExpenseSheet.validateId(id),
      ExpenseSheet.validateEmployee(employee),
      validateExpenses(expenses)).mapN(new ClaimedExpenseSheet(_, _, _))

  def create(employee: Employee, expenses:List[Expense]) : Validation.Result[ClaimedExpenseSheet] =
    create(UUID.randomUUID(), employee, expenses)
}