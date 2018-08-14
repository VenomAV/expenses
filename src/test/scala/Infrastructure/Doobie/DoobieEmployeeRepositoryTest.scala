package Infrastructure.Doobie

import Expenses.Model.Employee
import Expenses.Model.Employee.EmployeeId
import Expenses.Repositories.{EmployeeRepository, EmployeeRepositoryME}
import Infrastructure.Repositories.DoobieEmployeeRepositoryME
import Infrastructure.{EmployeeRepositoryContractTest, EmployeeRepositoryMEContractTest}
import cats.effect.IO
import doobie.free.connection.ConnectionIO
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.transactor.Transactor
import doobie.util.transactor.Transactor.Aux

class DoobieEmployeeRepositoryTest extends EmployeeRepositoryContractTest[ConnectionIO] with BaseDoobieTest {
  override protected def beforeEach(): Unit = {
    super.beforeEach()
    setUpDatabase()
  }

  override protected def afterEach(): Unit = {
    super.afterEach()
    cleanUpDatabase()
  }

  override def createRepositoryWith(employees: List[Employee]): EmployeeRepository[ConnectionIO] =
    createRepositoriesWith(List(), employees)._2

  override def cleanUp(employeeIds: List[EmployeeId]): Unit = cleanUp(List(), employeeIds)
}

class DoobieEmployeeRepositoryMETest extends EmployeeRepositoryMEContractTest[ConnectionIO] {
  implicit var xa: Aux[IO, Unit] = _
  var employeeIds: List[EmployeeId] = _

  def setUpDatabase(): Unit = {
    xa = Transactor.fromDriverManager[IO](
      "org.postgresql.Driver",
      "jdbc:postgresql:postgres",
      "postgres",
      "p4ssw0r#"
    )
  }
  override protected def beforeEach(): Unit = {
    super.beforeEach()
    setUpDatabase()
  }

  override protected def afterEach(): Unit = {
    super.afterEach()
    employeeIds.map(deleteEmployee)
  }

  override def createRepositoryWith(employees: List[Employee]): EmployeeRepositoryME[ConnectionIO] = {
    val employeeRepository = new DoobieEmployeeRepositoryME

    employeeIds = employees.map(_.id)
    employees
      .foreach(employeeRepository.save(_).transact(xa).unsafeRunSync())
    employeeRepository
  }

  override def cleanUp(employeeIds: List[EmployeeId]): Unit =
    employeeIds.map(deleteEmployee)

  override def run[A](toBeExecuted: ConnectionIO[A]): Either[Throwable, A] =
    toBeExecuted.transact(xa).attempt.unsafeRunSync()

  def deleteEmployee(id: EmployeeId) : Int = sql"delete from employees where id=$id".update.run.transact(xa).unsafeRunSync
}
