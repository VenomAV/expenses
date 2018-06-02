package Expenses.TestUtils

import java.util.UUID

import Expenses.Model.Employee
import Expenses.TestUtils.AcceptanceTestUtils.TestState
import org.scalatest.{FunSpec, Matchers}

class InMemoryEmployeeRepositoryTest extends FunSpec with Matchers {
  describe("get") {
    it("should retrieve existing element") {
      val employee = Employee(UUID.randomUUID(), "A", "V")
      implicit val state = TestState(
        List(employee),
        List(),
        List())
      val er = new InMemoryEmployeeRepository

      er.get(employee.id).runA(state).value should equal(Some(employee))
    }
  }
}
