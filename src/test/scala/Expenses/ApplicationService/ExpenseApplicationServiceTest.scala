package Expenses.ApplicationService

import java.util.UUID

import Expenses.ApplicationServices.ExpenseApplicationService
import Expenses.Model.{Employee, OpenExpenseSheet}
import Expenses.TestUtils.AcceptanceTestUtils.{Test, TestState}
import Expenses.TestUtils.{InMemoryEmployeeRepository, InMemoryExpenseSheetRepository}
import Expenses.Utils.Validation.Result
import cats.data.NonEmptyList
import cats.data.Validated.Invalid
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}

class ExpenseApplicationServiceTest extends FunSpec with Matchers with BeforeAndAfter {
  implicit var er: InMemoryEmployeeRepository = _
  implicit var esr: InMemoryExpenseSheetRepository = _

  before {
    er = new InMemoryEmployeeRepository()
    esr = new InMemoryExpenseSheetRepository()
  }
  describe("openFor") {
    it("should save a new OpenExpenseSheet for the given employee") {
      val employee = Employee(UUID.randomUUID(), "A", "V")
      val state = TestState(
        List(employee),
        List(),
        List())

      ExpenseApplicationService.openFor[Test](employee.id)
        .runS(state).value.expenseSheets.head should matchPattern {
          case OpenExpenseSheet(_, `employee`, List()) =>
        }
    }
    it("should not create an expense sheet when employee does not exist") {
      val state = TestState(List(), List(), List())

      val result: (TestState, Result[Unit]) = ExpenseApplicationService.openFor[Test](UUID.randomUUID())
        .run(state).value

      result._1 should equal(state)
      result._2 should matchPattern {
        case Invalid(NonEmptyList("Unable to find employee", _)) =>
      }
    }
  }
}
