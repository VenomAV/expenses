package Expenses.ApplicationServices

import Expenses.Model.Employee.EmployeeId
import Expenses.Model.Expense
import Expenses.Model.ExpenseSheet.ExpenseSheetId
import Expenses.Repositories.{ClaimRepository, EmployeeRepository, ExpenseSheetRepository}
import Expenses.Services.ExpenseService
import Expenses.Utils.Validation
import cats._
import cats.data.NonEmptyList
import cats.implicits._

object ExpenseApplicationService {
  def openFor[F[_]](id: EmployeeId)
                   (implicit M:Monad[F],
                    er: EmployeeRepository[F],
                    esr: ExpenseSheetRepository[F]) : F[Validation.Result[Unit]] =
    er.get(id)
      .map(x => for {
        employee <- x.toRight(NonEmptyList.of("Unable to find employee"))
        expenseSheet <- ExpenseService.openFor(employee).toEither
      } yield {
        expenseSheet
      }).flatMap({
        case Left(x) => M.pure(x.invalid)
        case Right(x) => esr.save(x).map(_.valid)
      })

  def addExpenseTo[F[_]](expense: Expense, id: ExpenseSheetId)
                        (implicit esr: ExpenseSheetRepository[F]) : F[Validation.Result[Unit]] = ???

  def claim[F[_]](id: ExpenseSheetId)
                 (implicit esr: ExpenseSheetRepository[F],
                  cr: ClaimRepository[F]) : F[Validation.Result[Unit]] = ???
}