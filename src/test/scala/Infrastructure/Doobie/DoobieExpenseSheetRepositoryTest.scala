package Infrastructure.Doobie

import Expenses.Model.Employee.EmployeeId
import Expenses.Model.ExpenseSheet.ExpenseSheetId
import Expenses.Model.{Employee, ExpenseSheet}
import Expenses.Repositories.ExpenseSheetRepository
import Infrastructure.ExpenseSheetRepositoryContractTest
import Infrastructure.Repositories.{DoobieEmployeeRepository, DoobieExpenseSheetRepository}
import cats.effect.IO
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.transactor.Transactor
import doobie.util.transactor.Transactor.Aux

class DoobieExpenseSheetRepositoryTest extends ExpenseSheetRepositoryContractTest[IO] {
  implicit var xa: Aux[IO, Unit] = _
  var employeeIds : List[EmployeeId] = _
  var expenseSheetIds : List[ExpenseSheetId] = _

  override protected def beforeEach(): Unit =  {
    super.beforeEach()
      xa = Transactor.fromDriverManager[IO](
      "org.postgresql.Driver",
      "jdbc:postgresql:postgres",
      "postgres",
      "p4ssw0r#"
    )
  }

  override def afterEach(): Unit = {
    super.afterEach()
    cleanUp(expenseSheetIds, employeeIds)
  }

  override def createRepositoryWith(expenseSheets: List[ExpenseSheet], employees: List[Employee]):
      ExpenseSheetRepository[IO] = {
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
    expenseSheetRepository
  }

  override def run[A](toBeExecuted: IO[A]): A =  toBeExecuted.unsafeRunSync

  override def cleanUp(expenseSheetIds: List[ExpenseSheetId], employeeIds: List[EmployeeId]): Unit = {
    expenseSheetIds.map(x => sql"delete from expensesheets where id=$x".update.run.transact(xa).unsafeRunSync)
    employeeIds.map(x => sql"delete from employees where id=$x".update.run.transact(xa).unsafeRunSync)
  }
}
