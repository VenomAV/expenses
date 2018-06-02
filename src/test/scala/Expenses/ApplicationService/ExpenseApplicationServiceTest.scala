package Expenses.ApplicationService

import java.util.UUID

import Expenses.ApplicationServices.ExpenseApplicationService
import Expenses.Model.Employee.EmployeeId
import Expenses.Model.{Employee, OpenExpenseSheet}
import Expenses.TestUtils.AcceptanceTestUtils.{Test, TestState}
import Expenses.TestUtils.{InMemoryEmployeeRepository, InMemoryExpenseSheetRepository}
import Expenses.Utils.ErrorManagement.ValidationResult
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
      val state = TestState(List(employee), List(), List())
      val newState = runOpenFor(employee.id, state)._1

      newState.expenseSheets should matchPattern {
        case List(OpenExpenseSheet(_, `employee`, List())) =>
      }
    }
    it("should not create an expense sheet when employee does not exist") {
      val state = TestState(List(), List(), List())
      val (newState, result) = runOpenFor(UUID.randomUUID(), state)

      newState should equal(state)
      result should matchPattern {
        case Invalid(NonEmptyList("Unable to find employee", _)) =>
      }
    }
  }

  private def runOpenFor(employeeId: EmployeeId, state: TestState) : (TestState, ValidationResult[Unit]) =
    ExpenseApplicationService.openFor[Test](employeeId).run(state).value
}
