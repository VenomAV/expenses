package Infrastructure

import java.util.UUID

import Expenses.Model.Employee
import Expenses.Model.Employee.EmployeeId
import Expenses.Repositories.EmployeeRepository
import cats.Monad
import org.scalatest.{FunSpec, Matchers}
import cats.syntax.functor._
import cats.syntax.flatMap._

abstract class EmployeeRepositoryContractTest[F[_]](implicit M:Monad[F]) extends FunSpec with Matchers {
  val testId: EmployeeId = UUID.randomUUID()

  describe("get") {
    it("should retrieve existing element") {
      val name = s"Andrea $testId"
      val surname = s"Vallotti $testId"
      val sut = createRepositoryWith(List(Employee(testId, s"Andrea $testId", s"Vallotti $testId")))

      run(sut.get(testId)) should matchPattern {
        case Some(Employee(`testId`, `name`, `surname`)) =>
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

  def createRepositoryWith(employees: List[Employee]): EmployeeRepository[F]

  def run[A](toBeExecuted: F[A]) : A
}
