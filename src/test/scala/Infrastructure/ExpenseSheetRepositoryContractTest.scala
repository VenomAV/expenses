package Infrastructure

import java.util.{Date, UUID}

import Expenses.Model._
import Expenses.Repositories.ExpenseSheetRepository
import cats.Monad
import org.scalatest.{FunSpec, Matchers}
import squants.market.Money

abstract class ExpenseSheetRepositoryContractTest[F[_]](implicit M:Monad[F]) extends FunSpec with Matchers {
  describe ("get") {
    it("should retrieve existing open expense sheet") {
      val id = UUID.randomUUID()
      val employee = Employee(UUID.randomUUID(), "Andrea", "Vallotti")
      val sut = createRepositoryWith(List(OpenExpenseSheet(id, employee, List())))

      run(sut.get(id)) should matchPattern {
        case Some(OpenExpenseSheet(`id`, `employee`, List())) =>
      }
    }
    it("should retrieve existing open expense sheet w/ expenses") {
      val id = UUID.randomUUID()
      val employee = Employee(UUID.randomUUID(), "Andrea", "Vallotti")
      val expenses = List(TravelExpense(Money(1, "EUR"), new Date(), "Florence", "Barcelona"))
      val sut = createRepositoryWith(List(OpenExpenseSheet(id, employee, expenses)))

      run(sut.get(id)) should matchPattern {
        case Some(OpenExpenseSheet(`id`, `employee`, `expenses`)) =>
      }
    }
  }

  def createRepositoryWith(expenseSheets: List[ExpenseSheet]): ExpenseSheetRepository[F]

  def run[A](toBeExecuted: F[A]) : A
}
