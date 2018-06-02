package Expenses.ApplicationService

import java.util.UUID

import Expenses.ApplicationServices.ExpenseApplicationService
import Expenses.Model.Employee.EmployeeId
import Expenses.Model.{Employee, OpenExpenseSheet}
import Expenses.TestUtils.{InMemoryEmployeeRepository, InMemoryExpenseSheetRepository, InMemoryMissingEmployeeRepository}
import cats.data.NonEmptyList
import cats.data.Validated.Invalid
import org.scalatest.{FunSpec, Matchers}

class ExpenseApplicationServiceTest extends FunSpec with Matchers {
  describe("openFor") {
    it("should save a new OpenExpenseSheet for the given employee") {
      implicit val er = new InMemoryEmployeeRepository()
      implicit val esr = new InMemoryExpenseSheetRepository()
      val employeeId : EmployeeId = UUID.randomUUID()

      ExpenseApplicationService.openFor(employeeId)
      esr.savedExpenseSheet should matchPattern {
        case Some(OpenExpenseSheet(_, Employee(`employeeId`, _, _), List())) =>
      }
    }
    it("should not create an expense sheet when employee does not exist") {
      implicit val er = new InMemoryMissingEmployeeRepository()
      implicit val esr = new InMemoryExpenseSheetRepository()
      val employeeId : EmployeeId = UUID.randomUUID()

      ExpenseApplicationService.openFor(employeeId) should matchPattern {
        case Invalid(NonEmptyList("Unable to find employee", _)) =>
      }
      esr.savedExpenseSheet should matchPattern { case None => }
    }
  }
}
