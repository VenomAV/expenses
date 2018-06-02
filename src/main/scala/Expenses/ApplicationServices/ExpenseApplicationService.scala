package Expenses.ApplicationServices

import Expenses.Model.Employee.EmployeeId
import Expenses.Model.Expense
import Expenses.Model.ExpenseSheet.ExpenseSheetId
import Expenses.Repositories.{ClaimRepository, EmployeeRepository, ExpenseSheetRepository}
import Expenses.Services.ExpenseService
import Expenses.Utils.Validation
import cats._
import cats.implicits._

object ExpenseApplicationService {
  def openFor[F[_]](id: EmployeeId)
                   (implicit M:Monad[F],
                    er: EmployeeRepository[F],
                    esr: ExpenseSheetRepository[F]) : F[Validation.Result[Unit]] =
    for {
      employee <- er.get(id)
    } yield employee match {
      case None => "Unable to find employee".invalidNel
      case Some(e) => ExpenseService.openFor(e).map(esr.save(_))
    }

  def addExpenseTo[F[_]](expense: Expense, id: ExpenseSheetId)
                        (implicit esr: ExpenseSheetRepository[F]) : F[Validation.Result[Unit]] = ???

  def claim[F[_]](id: ExpenseSheetId)
                 (implicit esr: ExpenseSheetRepository[F],
                  cr: ClaimRepository[F]) : F[Validation.Result[Unit]] = ???
}