package Infrastructure.Doobie

import java.util.UUID

import Expenses.Model.Employee
import Expenses.Model.Employee.EmployeeId
import Infrastructure.Repositories.DoobieEmployeeRepository
import cats.effect.IO
import doobie.Transactor
import doobie.util.transactor.Transactor.Aux
import doobie.implicits._
import doobie.postgres.implicits._
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}

class DoobieEmployeeRepositoryTest extends FunSpec with Matchers with BeforeAndAfter {
  var testId: EmployeeId = _
  implicit var xa: Aux[IO, Unit] = _

  before {
    testId = UUID.randomUUID()
    xa = Transactor.fromDriverManager[IO](
      "org.postgresql.Driver",
      "jdbc:postgresql:postgres",
      "postgres",
      "p4ssw0r#"
    )
  }
  describe("insert") {
    it("should work") {
      val id = testId
      val name = s"Andrea $id"
      val surname = s"Vallotti $id"
      val sut = new DoobieEmployeeRepository

      sut.insert(Employee(id, name, surname)).unsafeRunSync

      val employee = sut.get(id).unsafeRunSync

      employee should matchPattern {
        case Some(Employee(`id`, `name`, `surname`)) =>
      }
    }
    after {
      sql"delete from employees where id=$testId".update.run.transact(xa).unsafeRunSync
    }
  }
}
