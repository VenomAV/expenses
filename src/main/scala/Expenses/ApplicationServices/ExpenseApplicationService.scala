package Expenses.ApplicationServices

import Expenses.Model.Employee.EmployeeId
import Expenses.Model.ExpenseSheet.ExpenseSheetId
import Expenses.Model.{Expense, OpenExpenseSheet}
import Expenses.Repositories.{ClaimRepository, EmployeeRepository, ExpenseSheetRepository}
import Expenses.Services.ExpenseService
import Expenses.Utils.ErrorManagement.implicits._
import Expenses.Utils.ErrorManagement.{ApplicationResult, Error, ErrorList}
import cats._
import cats.implicits._
import cats.data.EitherT

import scala.reflect.ClassTag

object ExpenseApplicationService {
  def openFor[F[_]](id: EmployeeId)
                   (implicit M:Monad[F],
                    er: EmployeeRepository[F],
                    esr: ExpenseSheetRepository[F]) : F[ApplicationResult[Unit]] =
    (for {
      employee <- er.get(id).orErrorT(s"Unable to find employee $id")
      openExpenseSheet <- ExpenseService.openFor(employee).toEitherT[F]
      result <- esr.save(openExpenseSheet).rightT
    } yield result).value

  def addExpenseTo[F[_]](expense: Expense, id: ExpenseSheetId)
                        (implicit M:Monad[F],
                         esr: ExpenseSheetRepository[F]) : F[ApplicationResult[Unit]] =
    (for {
      openExpenseSheet <- getOpenExpenseSheet[F](id)
      newOpenExpenseSheet <- ExpenseService.addExpenseTo(expense, openExpenseSheet).toEitherT[F]
      result <- esr.save(newOpenExpenseSheet).rightT
    } yield result).value

  def claim[F[_]](id: ExpenseSheetId)
                 (implicit M:Monad[F],
                  esr: ExpenseSheetRepository[F],
                  cr: ClaimRepository[F]) : F[ApplicationResult[Unit]] =
    (for {
      openExpenseSheet <- getOpenExpenseSheet[F](id)
      pair <- ExpenseService.claim(openExpenseSheet).toEitherT[F]
      (claimedExpenseSheet, pendingClaim) = pair
      _ <- esr.save(claimedExpenseSheet).rightT
      _ <- cr.save(pendingClaim).rightT
    } yield ()).value

  private def getOpenExpenseSheet[F[_]](id: ExpenseSheetId)
                                       (implicit M:Monad[F],
                                        esr: ExpenseSheetRepository[F]): EitherT[F, ErrorList, OpenExpenseSheet] =
    for {
      expenseSheet <- esr.get(id).orErrorT("Unable to find expense sheet")
      openExpenseSheet <- tryCastTo[OpenExpenseSheet](expenseSheet,s"$id is not an open expense sheet").toEitherT[F]
    } yield openExpenseSheet

  private def tryCastTo[A : ClassTag](a: Any, error: Error) : ApplicationResult[A] = a match {
    case b: A => Right(b)
    case _ => Left(ErrorList.of(error))
  }
}