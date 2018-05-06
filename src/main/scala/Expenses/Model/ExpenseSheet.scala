package Expenses.Model

import java.util.UUID

import Expenses.Model.ExpenseSheet.ExpenseSheetId
import Expenses.Utils.Validation
import scalaz.Scalaz._
import scalaz.ValidationNel

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

  private val validateId = Validation.notNull[ExpenseSheetId]("id is null")(_)
  private val validateEmployee = Validation.notNull[Employee]("employee is null")(_)

  def validate(id: ExpenseSheetId, employee: Employee, expenses:List[Expense]) =
    validateId(id) |@| validateEmployee(employee)

}

object OpenExpenseSheet {
  def create(id: ExpenseSheetId, employee: Employee, expenses:List[Expense]) :
    ValidationNel[String, OpenExpenseSheet] =
    ExpenseSheet.validate(id, employee, expenses){ new OpenExpenseSheet(_, _, expenses) }

  def create(employee: Employee, expenses:List[Expense]) : ValidationNel[String, OpenExpenseSheet] =
    create(UUID.randomUUID(), employee, expenses)
}

object ClaimedExpenseSheet {
  private val validateExpenses = Validation.nonEmptyList[Expense]("expenses is empty")(_)


  def create(id: ExpenseSheetId, employee: Employee, expenses:List[Expense]) :
    ValidationNel[String, ClaimedExpenseSheet] =
    (ExpenseSheet.validate(id, employee, expenses) |@|
      validateExpenses(expenses)) { new ClaimedExpenseSheet(_, _, _) }

  def create(employee: Employee, expenses:List[Expense]) : ValidationNel[String, ClaimedExpenseSheet] =
    create(UUID.randomUUID(), employee, expenses)
}