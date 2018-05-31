package Expenses.Model

import java.util.UUID

import Expenses.Model.Claim.ClaimId
import Expenses.Utils.Validation
import cats.data.NonEmptyList
import cats.implicits._

sealed trait Claim {
  def Id: ClaimId
  def employee: Employee
  def expenses: NonEmptyList[Expense]
}

case class PendingClaim private (id: ClaimId, employee: Employee, expenses: NonEmptyList[Expense])

object Claim {
  type ClaimId = UUID
}

object PendingClaim {
  def create(employee: Employee, expenses: NonEmptyList[Expense]) : Validation.Result[PendingClaim] =
    new PendingClaim(UUID.randomUUID(), employee, expenses).validNel
}