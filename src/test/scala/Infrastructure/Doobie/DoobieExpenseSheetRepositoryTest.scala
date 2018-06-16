package Infrastructure.Doobie

import Expenses.Model.Employee.EmployeeId
import Expenses.Model.ExpenseSheet
import Expenses.Model.ExpenseSheet.ExpenseSheetId
import Expenses.Repositories.ExpenseSheetRepository
import Infrastructure.ExpenseSheetRepositoryContractTest
import Infrastructure.Repositories.{DoobieEmployeeRepository, DoobieExpenseSheetRepository}
import cats.effect.IO
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.transactor.Transactor
import doobie.util.transactor.Transactor.Aux
import org.scalatest.BeforeAndAfter

class DoobieExpenseSheetRepositoryTest extends ExpenseSheetRepositoryContractTest[IO] with BeforeAndAfter{
  implicit var xa: Aux[IO, Unit] = _
  var employeeIds : List[EmployeeId] = _
  var expenseSheetIds : List[ExpenseSheetId] = _

  before {
    xa = Transactor.fromDriverManager[IO](
      "org.postgresql.Driver",
      "jdbc:postgresql:postgres",
      "postgres",
      "p4ssw0r#"
    )
  }
  after {
    expenseSheetIds.map(x => sql"delete from expensesheets where id=$x".update.run.transact(xa).unsafeRunSync)
    employeeIds.map(x => sql"delete from employees where id=$x".update.run.transact(xa).unsafeRunSync)
  }

  override def createRepositoryWith(expenseSheets: List[ExpenseSheet]): ExpenseSheetRepository[IO] = {
    val employeeRepository = new DoobieEmployeeRepository
    val expenseSheetRepository = new DoobieExpenseSheetRepository()

    employeeIds = expenseSheets.map(_.employee.id)
    expenseSheetIds = expenseSheets.map(_.id)

    expenseSheets
      .map(es => for {
        _ <- employeeRepository.save(es.employee)
        _ <- expenseSheetRepository.save(es)
      } yield ())
      .foreach(_.unsafeRunSync)
    expenseSheetRepository
  }

  override def run[A](toBeExecuted: IO[A]): A =  toBeExecuted.unsafeRunSync
}
