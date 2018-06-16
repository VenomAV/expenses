package Infrastructure.Repositories

import Expenses.Model.Employee
import Expenses.Model.Employee.EmployeeId
import Expenses.Repositories.EmployeeRepository
import cats.effect.IO
import doobie.util.transactor.Transactor.Aux
import doobie.implicits._
import doobie.postgres.implicits._

class DoobieEmployeeRepository(implicit xa: Aux[IO, Unit]) extends EmployeeRepository[IO] {
  override def get(id: EmployeeId): IO[Option[Employee]] =
    sql"select * from employees where id=$id".query[Employee].option.transact(xa)

  override def insert(employee: Employee): IO[Unit] =
    sql"insert into employees (id, name, surname) values (${employee.id}, ${employee.name}, ${employee.surname})"
      .update.run.map(_ => ()).transact(xa)
}
