package Expenses.Model

import java.util.UUID

import Expenses.Model.ExpenseSheet.ExpenseSheetId
import Expenses.Utils.Validation
import scalaz.{NonEmptyList, ValidationNel}
import scalaz.Scalaz._

sealed trait ExpenseSheet {
  def id: ExpenseSheetId
  def employee: Employee
  def expenses: NonEmptyList[Expense]
}

case class OpenExpenseSheet private (id: ExpenseSheetId,
                                     employee: Employee,
                                     expenses:NonEmptyList[Expense]) extends ExpenseSheet

case class ClaimedExpenseSheet private (id: ExpenseSheetId,
                                        employee: Employee,
                                        expenses:NonEmptyList[Expense]) extends ExpenseSheet

object ExpenseSheet {
  type ExpenseSheetId = UUID

  private val validateId = Validation.notNull[ExpenseSheetId]("id is null")(_)
  private val validateEmployee = Validation.notNull[Employee]("employee is null")(_)

  def validate(id: ExpenseSheetId, employee: Employee, expenses:NonEmptyList[Expense]) =
    validateId(id) |@| validateEmployee(employee) |@| expenses.successNel

}

object OpenExpenseSheet {
  def create(id: ExpenseSheetId, employee: Employee, expenses:NonEmptyList[Expense]) :
    ValidationNel[String, OpenExpenseSheet] =
    ExpenseSheet.validate(id, employee, expenses){ new OpenExpenseSheet(_, _, _) }

  def create(employee: Employee, expenses:NonEmptyList[Expense]) : ValidationNel[String, OpenExpenseSheet] =
    create(UUID.randomUUID(), employee, expenses)
}

object ClaimedExpenseSheet {
  def create(id: ExpenseSheetId, employee: Employee, expenses:NonEmptyList[Expense]) :
    ValidationNel[String, ClaimedExpenseSheet] =
    ExpenseSheet.validate(id, employee, expenses) { new ClaimedExpenseSheet(_, _, _) }

  def create(employee: Employee, expenses:NonEmptyList[Expense]) : ValidationNel[String, ClaimedExpenseSheet] =
    create(UUID.randomUUID(), employee, expenses)
}