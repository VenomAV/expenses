package Expenses.ApplicationServices

import Expenses.Model.Employee.EmployeeId
import Expenses.Model.ExpenseSheet.ExpenseSheetId
import Expenses.Model.{Expense, OpenExpenseSheet}
import Expenses.Repositories.{ClaimRepository, EmployeeRepository, ExpenseSheetRepository}
import Expenses.Services.ExpenseService
import Expenses.Utils.ErrorManagement.implicits._
import Expenses.Utils.ErrorManagement.{ApplicationResult, Error, ErrorList}
import cats._
import cats.data.EitherT

import scala.reflect.ClassTag

object ExpenseApplicationService {
  def openFor[F[_]](id: EmployeeId)
                   (implicit M:Monad[F],
                    er: EmployeeRepository[F],
                    esr: ExpenseSheetRepository[F]) : F[ApplicationResult[Unit]] =
    (for {
      employee <- EitherT(er.get(id).orError(s"Unable to find employee $id"))
      openExpenseSheet <- EitherT.fromEither[F](ExpenseService.openFor(employee).toEither)
      result <- EitherT.right[ErrorList](esr.save(openExpenseSheet))
    } yield result).value

  def addExpenseTo[F[_]](expense: Expense, id: ExpenseSheetId)
                        (implicit M:Monad[F],
                         esr: ExpenseSheetRepository[F]) : F[ApplicationResult[Unit]] =
    (for {
      openExpenseSheet <- getOpenExpenseSheet[F](id)
      newOpenExpenseSheet <- EitherT.fromEither[F](ExpenseService.addExpenseTo(expense, openExpenseSheet).toEither)
      result <- EitherT.right[ErrorList](esr.save(newOpenExpenseSheet))
    } yield result).value

  def claim[F[_]](id: ExpenseSheetId)
                 (implicit M:Monad[F],
                  esr: ExpenseSheetRepository[F],
                  cr: ClaimRepository[F]) : F[ApplicationResult[Unit]] =
    (for {
      openExpenseSheet <- getOpenExpenseSheet[F](id)
      pair <- EitherT.fromEither[F](ExpenseService.claim(openExpenseSheet).toEither)
      (claimedExpenseSheet, pendingClaim) = pair
      _ <- EitherT.right[ErrorList](esr.save(claimedExpenseSheet))
      _ <- EitherT.right[ErrorList](cr.save(pendingClaim))
    } yield ()).value

  private def getOpenExpenseSheet[F[_]](id: ExpenseSheetId)
                                       (implicit M:Monad[F],
                                        esr: ExpenseSheetRepository[F]): EitherT[F, ErrorList, OpenExpenseSheet] =
    for {
      expenseSheet <- EitherT(esr.get(id).orError("Unable to find expense sheet"))
      openExpenseSheet <- EitherT.fromEither[F](tryCastTo[OpenExpenseSheet](expenseSheet,s"$id is not an open expense sheet"))
    } yield openExpenseSheet

  private def tryCastTo[A : ClassTag](a: Any, error: Error) : ApplicationResult[A] = a match {
    case b: A => Right(b)
    case _ => Left(ErrorList.of(error))
  }
}