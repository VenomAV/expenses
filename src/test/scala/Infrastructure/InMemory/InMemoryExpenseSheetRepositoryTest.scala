package Infrastructure.InMemory

import Expenses.Model.Employee.EmployeeId
import Expenses.Model.ExpenseSheet.ExpenseSheetId
import Expenses.Model.{Employee, ExpenseSheet}
import Expenses.Repositories.ExpenseSheetRepository
import Expenses.TestUtils.AcceptanceTestUtils.{Test, TestState}
import Expenses.TestUtils.InMemoryExpenseSheetRepository
import Infrastructure.ExpenseSheetRepositoryContractTest

class InMemoryExpenseSheetRepositoryTest extends ExpenseSheetRepositoryContractTest[Test] {
  implicit var state : TestState = _

  override def createRepositoryWith(expenseSheets: List[ExpenseSheet], employees: List[Employee]):
      ExpenseSheetRepository[Test] = {
    state = TestState(
      employees,
      expenseSheets,
      List())
    new InMemoryExpenseSheetRepository
  }

  override def run[A](toBeExecuted: Test[A]): A = toBeExecuted.runA(state).value

  override def cleanUp(expenseSheetIds: List[ExpenseSheetId]): Unit = ()
}
