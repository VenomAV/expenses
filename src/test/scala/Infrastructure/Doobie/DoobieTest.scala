package Infrastructure.Doobie

import java.util.UUID

import Expenses.Model.Employee
import Expenses.Model.Employee.EmployeeId
import cats.effect._
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.transactor.Transactor.Aux

class DoobieTest extends FunSpec with Matchers with BeforeAndAfter{
  var testId: EmployeeId = _
  var xa: Aux[IO, Unit] = _

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

      inserEmployee(id, name, surname).transact(xa).unsafeRunSync

      val employee = selectEmployee(id).transact(xa).unsafeRunSync

      employee should matchPattern {
        case Some(Employee(`id`, `name`, `surname`)) =>
      }
    }
    after {
      sql"delete from employees where id=$testId".update.run.transact(xa).unsafeRunSync
    }
  }

  def inserEmployee(id: EmployeeId, name: String, surname: String): ConnectionIO[Unit] =
    sql"insert into employees (id, name, surname) values ($id, $name, $surname)"
      .update
      .withUniqueGeneratedKeys[Employee]("id", "name", "surname")
      .map(_ => ())

  def selectEmployee(id: EmployeeId): ConnectionIO[Option[Employee]] =
    sql"select * from employees where id=$id".query[Employee].option
}
