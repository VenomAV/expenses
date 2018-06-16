package Infrastructure.InMemory

import java.util.UUID

import Expenses.Model.{Employee, OpenExpenseSheet}
import Expenses.TestUtils.AcceptanceTestUtils.TestState
import Expenses.TestUtils.InMemoryExpenseSheetRepository
import org.scalatest.{FunSpec, Matchers}

class InMemoryExpenseSheetRepositoryTest extends FunSpec with Matchers {
  describe("save") {
    it("should modify state") {
      implicit val state = TestState(
        List(),
        List(),
        List())
      implicit val esr = new InMemoryExpenseSheetRepository()
      val employee = Employee(UUID.randomUUID(), "A", "V")
      val expenseSheet = OpenExpenseSheet(UUID.randomUUID(), employee, List())

      esr.save(expenseSheet).runS(state).value.expenseSheets should contain (expenseSheet)
    }
  }
}
