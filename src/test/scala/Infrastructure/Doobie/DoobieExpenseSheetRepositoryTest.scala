package Infrastructure.Doobie

import Expenses.Model.ExpenseSheet.ExpenseSheetId
import Expenses.Model.{Employee, ExpenseSheet}
import Expenses.Repositories.ExpenseSheetRepository
import Infrastructure.ExpenseSheetRepositoryContractTest
import cats.effect.IO
import doobie.postgres.implicits._

class DoobieExpenseSheetRepositoryTest extends ExpenseSheetRepositoryContractTest[IO] with BaseDoobieTest {

  override protected def beforeEach(): Unit =  {
    super.beforeEach()
    setUpDatabase()
  }

  override def afterEach(): Unit = {
    super.afterEach()
    cleanUpDatabase()
  }

  override def createRepositoryWith(expenseSheets: List[ExpenseSheet], employees: List[Employee]):
      ExpenseSheetRepository[IO] = createRepositoriesWith(expenseSheets, employees)._1

  override def cleanUp(expenseSheetIds: List[ExpenseSheetId]): Unit = cleanUp(expenseSheetIds, List())
}
