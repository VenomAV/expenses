package Infrastructure.Doobie

import Expenses.Model.ExpenseSheet
import Expenses.Repositories.ExpenseSheetRepository
import Infrastructure.ExpenseSheetRepositoryContractTest
import Infrastructure.Repositories.{DoobieEmployeeRepository, DoobieExpenseSheetRepository}
import cats.effect.IO
import doobie.postgres.implicits._
import doobie.util.transactor.Transactor
import doobie.util.transactor.Transactor.Aux
import org.scalatest.BeforeAndAfter

//trait BaseDoobieTest extends FunSpec with BeforeAndAfter {
//
//}

class DoobieExpenseSheetRepositoryTest extends ExpenseSheetRepositoryContractTest[IO] with BeforeAndAfter{
  implicit var xa: Aux[IO, Unit] = _

  before {
    xa = Transactor.fromDriverManager[IO](
      "org.postgresql.Driver",
      "jdbc:postgresql:postgres",
      "postgres",
      "p4ssw0r#"
    )
  }
  after {
  }

  override def createRepositoryWith(expenseSheets: List[ExpenseSheet]): ExpenseSheetRepository[IO] = {
    val employeeRepository = new DoobieEmployeeRepository
    val expenseSheetRepository = new DoobieExpenseSheetRepository()

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
