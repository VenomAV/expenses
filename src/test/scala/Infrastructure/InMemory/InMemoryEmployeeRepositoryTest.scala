package Infrastructure.InMemory

import Expenses.Model.Employee
import Expenses.Model.Employee.EmployeeId
import Expenses.Repositories.{EmployeeRepository, EmployeeRepositoryME}
import Expenses.TestUtils.AcceptanceTestUtils.{Test, TestME, TestState}
import Expenses.TestUtils.{InMemoryEmployeeMERepository, InMemoryEmployeeRepository}
import Expenses.Utils.ErrorManagement.ErrorList
import Infrastructure.{EmployeeRepositoryContractTest, EmployeeRepositoryMEContractTest}
import org.scalatest.BeforeAndAfter
import cats.data.StateT._
import cats.data._
import cats.implicits._

class InMemoryEmployeeRepositoryTest extends EmployeeRepositoryContractTest[Test] with BeforeAndAfter{
  implicit var state : TestState = _

  before {
    state = TestState(
      List(),
      List(),
      List())
  }

  override def createRepositoryWith(employees: List[Employee]): EmployeeRepository[Test] = {
    state = TestState(
      employees,
      List(),
      List())
    new InMemoryEmployeeRepository
  }

  override def run[A](executionUnit: Test[A]): A = executionUnit.runA(state).value

  override def cleanUp(employeeIds: List[EmployeeId]): Unit = ()
}

class InMemoryEmployeeRepositoryMETest extends EmployeeRepositoryMEContractTest[TestME] with BeforeAndAfter{
  implicit var state : TestState = _

  before {
    state = TestState(
      List(),
      List(),
      List())
  }

  override def createRepositoryWith(employees: List[Employee]): EmployeeRepositoryME[TestME] = {
    state = TestState(
      employees,
      List(),
      List())
    new InMemoryEmployeeMERepository
  }

  override def run[A](toBeExecuted: TestME[A]) : Either[Throwable, A] =
    toBeExecuted.runA(state)

  override def cleanUp(employeeIds: List[EmployeeId]): Unit = ()
}