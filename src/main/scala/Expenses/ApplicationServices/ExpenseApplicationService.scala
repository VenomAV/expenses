package Expenses.ApplicationServices

import Expenses.Model.Employee.EmployeeId
import Expenses.Model.{Expense, OpenExpenseSheet}
import Expenses.Model.ExpenseSheet.ExpenseSheetId
import Expenses.Repositories.{ClaimRepository, EmployeeRepository, ExpenseSheetRepository}
import Expenses.Services.ExpenseService
import Expenses.Utils.ErrorManagement.{ApplicationResult, ErrorList }
import Expenses.Utils.ErrorManagement.implicits._
import cats._
import cats.implicits._

object ExpenseApplicationService {
  def openFor[F[_]](id: EmployeeId)
                   (implicit M:Monad[F],
                    er: EmployeeRepository[F],
                    esr: ExpenseSheetRepository[F]) : F[ApplicationResult[Unit]] =
    er.get(id)
      .map(_.orError("Unable to find employee"))
      .map(_.flatMap(ExpenseService.openFor(_).toEither))
      .flatMap(_.traverse(esr.save(_)))

  def addExpenseTo[F[_]](expense: Expense, id: ExpenseSheetId)
                        (implicit M:Monad[F],
                         esr: ExpenseSheetRepository[F]) : F[ApplicationResult[Unit]] =
    esr.get(id)
      .map(_.orError("Unable to find expense sheet"))
      .map(_.flatMap({
        case oes: OpenExpenseSheet => Right(oes)
        case _ => Left(ErrorList.of(s"$id is not an open expense sheet"))
      }))
      .map(_.flatMap(ExpenseService.addExpenseTo(expense, _).toEither))
      .flatMap(_.traverse(esr.save(_)))

  def claim[F[_]](id: ExpenseSheetId)
                 (implicit M:Monad[F],
                  esr: ExpenseSheetRepository[F],
                  cr: ClaimRepository[F]) : F[ApplicationResult[Unit]] =
    esr.get(id)
      .map(_.orError("Unable to find expense sheet"))
      .map(_.flatMap({
        case oes: OpenExpenseSheet => Right(oes)
        case _ => Left(ErrorList.of(s"$id is not an open expense sheet"))
      }))
      .map(_.flatMap(ExpenseService.claim(_).toEither))
      .flatMap(_.traverse(pair => {
        cr.save(pair._2)
        esr.save(pair._1)
      }))
}