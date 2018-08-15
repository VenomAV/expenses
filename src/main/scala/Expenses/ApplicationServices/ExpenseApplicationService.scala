package Expenses.ApplicationServices

import Expenses.Model.Employee.EmployeeId
import Expenses.Model.ExpenseSheet.ExpenseSheetId
import Expenses.Model.{Expense, ExpenseSheet, OpenExpenseSheet}
import Expenses.Repositories._
import Expenses.Services.ExpenseService
import Expenses.Utils.ErrorManagement.implicits._
import cats._
import cats.implicits._

object ExpenseApplicationService {
  def openFor[F[_]](id: EmployeeId)
                   (implicit ME:MonadError[F, Throwable],
                    er: EmployeeRepository[F],
                    esr: ExpenseSheetRepository[F]) : F[Unit] =
    for {
      employee <- er.get(id)
      openExpenseSheet <- ExpenseService.openFor(employee).orRaiseError(ME)
      result <- esr.save(openExpenseSheet)
    } yield result

  def addExpenseTo[F[_]](expense: Expense, id: ExpenseSheetId)
                        (implicit ME:MonadError[F, Throwable],
                         esr: ExpenseSheetRepository[F]) : F[Unit] =
    for {
      openExpenseSheet <- getOpenExpenseSheet[F](id)
      newOpenExpenseSheet <- ExpenseService.addExpenseTo(expense, openExpenseSheet).orRaiseError(ME)
      result <- esr.save(newOpenExpenseSheet)
    } yield result

  def claim[F[_]](id: ExpenseSheetId)
                 (implicit ME:MonadError[F, Throwable],
                  esr: ExpenseSheetRepository[F],
                  cr: ClaimRepository[F]) : F[Unit] =
    for {
      openExpenseSheet <- getOpenExpenseSheet[F](id)
      pair <- ExpenseService.claim(openExpenseSheet).orRaiseError(ME)
      (claimedExpenseSheet, pendingClaim) = pair
      _ <- esr.save(claimedExpenseSheet)
      _ <- cr.save(pendingClaim)
    } yield ()

  private def getOpenExpenseSheet[F[_]](id: ExpenseSheetId)
                                       (implicit ME:MonadError[F, Throwable],
                                        esr: ExpenseSheetRepository[F]): F[OpenExpenseSheet] =
    for {
      expenseSheet <- esr.get(id)
      openExpenseSheet <- toOpenExpenseSheet(expenseSheet)(ME)
    } yield openExpenseSheet

  private def toOpenExpenseSheet[F[_]](es: ExpenseSheet)(implicit ME:MonadError[F, Throwable]) : F[OpenExpenseSheet] = es match {
    case b: OpenExpenseSheet => ME.pure(b)
    case _ => ME.raiseError(new Error(s"${es.id} is not an open expense sheet"))
  }
}