package Infrastructure.Doobie

import Expenses.Model.Employee
import Expenses.Model.Employee.EmployeeId
import Expenses.Repositories.EmployeeRepository
import Infrastructure.EmployeeRepositoryContractTest
import Infrastructure.Repositories.DoobieEmployeeRepository
import cats.effect.IO
import doobie.Transactor
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.transactor.Transactor.Aux
import org.scalatest.BeforeAndAfter

class DoobieEmployeeRepositoryTest extends EmployeeRepositoryContractTest[IO] with BeforeAndAfter {
  implicit var xa: Aux[IO, Unit] = _
  var employeeIds : List[EmployeeId] = _

  before {
    xa = Transactor.fromDriverManager[IO](
      "org.postgresql.Driver",
      "jdbc:postgresql:postgres",
      "postgres",
      "p4ssw0r#"
    )
  }

  override def createRepositoryWith(employees: List[Employee]): EmployeeRepository[IO] = {
    employeeIds = employees.map(_.id)
    employees
      .map(e => sql"insert into employees (id, name, surname) values (${e.id}, ${e.name}, ${e.surname})".update.run)
      .map(_.transact(xa))
      .map(_.unsafeRunSync)
    new DoobieEmployeeRepository
  }

  override def run[A](toBeExecuted: IO[A]): A = toBeExecuted.unsafeRunSync

  override def deleteEmployee(employeeId: EmployeeId): Unit =
    sql"delete from employees where id=$employeeId".update.run.transact(xa).unsafeRunSync

  override def cleanUp(): Unit = employeeIds.map(deleteEmployee)
}
