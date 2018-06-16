package Infrastructure.Repositories

import Expenses.Model.{Claim, PendingClaim}
import Expenses.Repositories.ClaimRepository
import Infrastructure.Repositories.Doobie.implicits._
import cats.effect.IO
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.transactor.Transactor.Aux

class DoobieClaimRepository(implicit xa: Aux[IO, Unit]) extends ClaimRepository[IO] {
  type ClaimType = String

  override def save(claim: Claim): IO[Unit] =
    sql"""insert into claims (id, type, employeeid, expenses)
            values (${claim.id}, ${claimType(claim)}, ${claim.employee.id}, ${claim.expenses.toList})"""
      .update.run.map(_ => ()).transact(xa)

  private def claimType(claim: Claim) : ClaimType = claim match {
    case PendingClaim(_, _, _) => "P"
    case _ => throw new UnsupportedOperationException
  }
}
