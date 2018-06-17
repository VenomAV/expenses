package Infrastructure.Doobie

import Expenses.Model.Employee.EmployeeId
import Expenses.Model.ExpenseSheet.ExpenseSheetId
import Expenses.Model.{Employee, ExpenseSheet}
import Expenses.Repositories.{EmployeeRepository, ExpenseSheetRepository}
import Infrastructure.Repositories.{DoobieEmployeeRepository, DoobieExpenseSheetRepository}
import cats.effect.IO
import doobie.util.transactor.Transactor
import doobie.util.transactor.Transactor.Aux
import doobie.implicits._
import doobie.postgres.implicits._

trait BaseDoobieTest {
  implicit var xa: Aux[IO, Unit] = _
  var employeeIds : List[EmployeeId] = _
  var expenseSheetIds : List[ExpenseSheetId] = _

  def setUpDatabase(): Unit = {
    xa = Transactor.fromDriverManager[IO](
      "org.postgresql.Driver",
      "jdbc:postgresql:postgres",
      "postgres",
      "p4ssw0r#"
    )
  }

  def cleanUpDatabase(): Unit = {
    cleanUp(expenseSheetIds, employeeIds)
  }

  def cleanUp(expenseSheetIds: List[ExpenseSheetId], employeeIds: List[EmployeeId]): Unit = {
    expenseSheetIds.map(x => sql"delete from expensesheets where id=$x".update.run.transact(xa).unsafeRunSync)
    employeeIds.map(x => sql"delete from employees where id=$x".update.run.transact(xa).unsafeRunSync)
  }

  def createRepositoriesWith(expenseSheets: List[ExpenseSheet], employees: List[Employee]):
      (ExpenseSheetRepository[IO], EmployeeRepository[IO]) = {
    val employeeRepository = new DoobieEmployeeRepository
    val expenseSheetRepository = new DoobieExpenseSheetRepository()

    employeeIds = employees.map(_.id)
    expenseSheetIds = expenseSheets.map(_.id)

    employees
      .map(employeeRepository.save)
      .foreach(_.unsafeRunSync)
    expenseSheets
      .map(expenseSheetRepository.save)
      .foreach(_.unsafeRunSync)
    (expenseSheetRepository, employeeRepository)
  }

  def run[A](toBeExecuted: IO[A]): A =  toBeExecuted.unsafeRunSync
}
