package Expenses.Model

import java.util.UUID

import Expenses.Model.Claim.ClaimId
import cats.data.NonEmptyList

sealed trait Claim {
  def Id: ClaimId
  def employee: Employee
  def expenses: NonEmptyList[Expense]
}

case class PendingClaim (id: ClaimId, employee: Employee, expenses: NonEmptyList[Expense])

object Claim {
  type ClaimId = UUID
}
