package Infrastructure

import java.util.UUID

import Expenses.Model.Employee
import Expenses.Model.Employee.EmployeeId
import Expenses.Repositories.EmployeeRepository
import cats.Monad
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}
import cats.syntax.functor._
import cats.syntax.flatMap._

abstract class EmployeeRepositoryContractTest[F[_]](implicit M:Monad[F]) extends FunSpec with Matchers with BeforeAndAfter {
  val testId: EmployeeId = UUID.randomUUID()

  describe("get") {
    it("should retrieve existing element") {
      val id = UUID.randomUUID()
      val name = s"Andrea $id"
      val surname = s"Vallotti $id"
      val sut = createRepositoryWith(List(Employee(id, name, surname)))

      run(sut.get(id)) should matchPattern {
        case Some(Employee(`id`, `name`, `surname`)) =>
      }
    }
  }
  describe("save") {
    it("should work") {
      val sut = createRepositoryWith(List())

      run(for {
        _ <- sut.save(Employee(testId, s"Andrea $testId", s"Vallotti $testId"))
        employee <- sut.get(testId)
      } yield employee) should matchPattern {
        case Some(Employee(_, _, _)) =>
      }
    }
  }
  after {
    deleteEmployee(testId)
    cleanUp()
  }

  def createRepositoryWith(employees: List[Employee]): EmployeeRepository[F]

  def run[A](toBeExecuted: F[A]) : A

  def deleteEmployee(employeeId: EmployeeId) : Unit

  def cleanUp() : Unit
}
