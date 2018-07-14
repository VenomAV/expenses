package Expenses.ApplicationService

import java.util.{Date, UUID}

import Expenses.ApplicationServices.ExpenseApplicationService
import Expenses.Model.Employee.EmployeeId
import Expenses.Model.ExpenseSheet.ExpenseSheetId
import Expenses.Model._
import Expenses.TestUtils.AcceptanceTestUtils.{Test, TestState}
import Expenses.TestUtils.{InMemoryClaimRepository, InMemoryEmployeeRepository, InMemoryExpenseSheetRepository}
import Expenses.Utils.ErrorManagement.ApplicationResult
import cats.data.NonEmptyList
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}
import squants.market.Money

class ExpenseApplicationServiceTest extends FunSpec with Matchers with BeforeAndAfter {
  implicit var er: InMemoryEmployeeRepository = _
  implicit var esr: InMemoryExpenseSheetRepository = _
  implicit var cr: InMemoryClaimRepository = _

  before {
    er = new InMemoryEmployeeRepository()
    esr = new InMemoryExpenseSheetRepository()
    cr = new InMemoryClaimRepository()
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
        case Left(NonEmptyList(s, _)) if s.toString.startsWith("Unable to find employee ") =>
      }
    }
  }
  describe("addExpenseTo") {
    it("should add an expense to an open expense sheet") {
      val employee = Employee(UUID.randomUUID(), "A", "V")
      val expense = TravelExpense(Money(1, "EUR"), new Date(), "Florence", "Barcelona")
      val expenseSheet = OpenExpenseSheet(UUID.randomUUID(), employee, List())
      val state = TestState(List(employee), List(expenseSheet), List())
      val id = expenseSheet.id
      val newState = runAddExpenseTo(expense, id, state)._1

      newState.expenseSheets should matchPattern {
        case List(OpenExpenseSheet(`id`, `employee`, List(`expense`))) =>
      }
    }
    it("should return error when expense sheet is missing") {
      val expense = TravelExpense(Money(1, "EUR"), new Date(), "Florence", "Barcelona")
      val state = TestState(List(), List(), List())
      val (newState, result) = runAddExpenseTo(expense, UUID.randomUUID(), state)

      newState should equal(state)
      result should matchPattern {
        case Left(NonEmptyList("Unable to find expense sheet", _)) =>
      }
    }
    it("should not add an expense to a claimed expense sheet") {
      val employee = Employee(UUID.randomUUID(), "A", "V")
      val expense = TravelExpense(Money(1, "EUR"), new Date(), "Florence", "Barcelona")
      val expenseSheet = ClaimedExpenseSheet(UUID.randomUUID(), employee, List(expense))
      val state = TestState(List(employee), List(expenseSheet), List())
      val (newState, result) = runAddExpenseTo(expense, expenseSheet.id, state)

      result should matchPattern {
        case Left(NonEmptyList(s, _)) if s.toString.endsWith(" is not an open expense sheet") =>
      }
      newState.expenseSheets should matchPattern {
        case List(`expenseSheet`) =>
      }
    }
  }
  describe("claim") {
    it("should save claimed expense sheet and pending claim") {
      val employee = Employee(UUID.randomUUID(), "A", "V")
      val expenses = List(TravelExpense(Money(1, "EUR"), new Date(), "Florence", "Barcelona"))
      val expenseSheet = OpenExpenseSheet(UUID.randomUUID(), employee, expenses)
      val state = TestState(List(employee), List(expenseSheet), List())
      val id = expenseSheet.id
      val newState = runClaim(id, state)._1

      newState.claims should matchPattern {
        case List(PendingClaim(_, `employee`, _)) =>
      }
      newState.expenseSheets should matchPattern {
        case List(ClaimedExpenseSheet(`id`, `employee`, `expenses`)) =>
      }
    }
  }

  private def runOpenFor(employeeId: EmployeeId, state: TestState) : (TestState, ApplicationResult[Unit]) =
    ExpenseApplicationService.openFor[Test](employeeId).run(state).value

  private def runAddExpenseTo(expense: Expense, expenseSheetId: ExpenseSheetId, state: TestState) =
    ExpenseApplicationService.addExpenseTo[Test](expense, expenseSheetId).run(state).value

  private def runClaim(expenseSheetId: ExpenseSheetId, state: TestState) : (TestState, ApplicationResult[Unit]) =
    ExpenseApplicationService.claim[Test](expenseSheetId).run(state).value
}
