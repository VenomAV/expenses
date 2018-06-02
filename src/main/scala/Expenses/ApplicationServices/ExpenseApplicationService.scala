package Expenses.ApplicationServices

import Expenses.Model.Employee.EmployeeId
import Expenses.Model.Expense
import Expenses.Model.ExpenseSheet.ExpenseSheetId
import Expenses.Repositories.{ClaimRepository, EmployeeRepository, ExpenseSheetRepository}
import Expenses.Services.ExpenseService
import Expenses.Utils.ErrorManagement.ValidationResult
import Expenses.Utils.ErrorManagement.implicits._
import cats._
import cats.implicits._

object ExpenseApplicationService {
  def openFor[F[_]](id: EmployeeId)
                   (implicit M:Monad[F],
                    er: EmployeeRepository[F],
                    esr: ExpenseSheetRepository[F]) : F[ValidationResult[Unit]] =
    er.get(id)
      .map(_.orError("Unable to find employee"))
      .map(_.flatMap(ExpenseService.openFor(_).toEither))
      .flatMap(_.traverse(esr.save(_)))
      .map(_.toValidated)

  def addExpenseTo[F[_]](expense: Expense, id: ExpenseSheetId)
                        (implicit esr: ExpenseSheetRepository[F]) : F[ValidationResult[Unit]] = ???

  def claim[F[_]](id: ExpenseSheetId)
                 (implicit esr: ExpenseSheetRepository[F],
                  cr: ClaimRepository[F]) : F[ValidationResult[Unit]] = ???
}