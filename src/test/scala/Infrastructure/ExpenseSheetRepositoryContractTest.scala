package Infrastructure

import java.util.{Date, UUID}

import Expenses.Model.Employee.EmployeeId
import Expenses.Model.ExpenseSheet.ExpenseSheetId
import Expenses.Model._
import Expenses.Repositories.ExpenseSheetRepository
import cats.Monad
import cats.syntax.flatMap._
import cats.syntax.functor._
import org.scalatest.{BeforeAndAfterEach, FunSpec, Matchers}
import squants.market.Money

import scala.collection.mutable.ListBuffer

abstract class ExpenseSheetRepositoryContractTest[F[_]](implicit M:Monad[F])
  extends FunSpec with Matchers with BeforeAndAfterEach {
  val toBeDeletedEmployeeIds: ListBuffer[EmployeeId] = ListBuffer.empty[EmployeeId]
  val toBeDeletedExpenseSheetIds: ListBuffer[ExpenseSheetId] = ListBuffer.empty[ExpenseSheetId]

  describe ("get") {
    it("should retrieve existing open expense sheet") {
      val id = UUID.randomUUID()
      val employee = Employee(UUID.randomUUID(), "Andrea", "Vallotti")
      val sut = createRepositoryWith(List(OpenExpenseSheet(id, employee, List())), List(employee))

      run(sut.get(id)) should matchPattern {
        case Some(OpenExpenseSheet(`id`, `employee`, List())) =>
      }
    }
    it("should retrieve existing open expense sheet w/ expenses") {
      val id = UUID.randomUUID()
      val employee = Employee(UUID.randomUUID(), "Andrea", "Vallotti")
      val expenses = List(
        TravelExpense(Money(1, "EUR"), new Date(), "Florence", "Barcelona"),
        FoodExpense(Money(2, "EUR"), new Date()),
        AccommodationExpense(Money(2, "EUR"), new Date(), "Artemide"),
        OtherExpense(Money(2, "EUR"), new Date(), "A very long description about how I spend money"))
      val sut = createRepositoryWith(List(OpenExpenseSheet(id, employee, expenses)), List(employee))

      run(sut.get(id)) should matchPattern {
        case Some(OpenExpenseSheet(`id`, `employee`, `expenses`)) =>
      }
    }
    it("should retrieve existing claimed expense sheet") {
      val id = UUID.randomUUID()
      val employee = Employee(UUID.randomUUID(), "Andrea", "Vallotti")
      val expenses = List(
        TravelExpense(Money(1, "EUR"), new Date(), "Florence", "Barcelona"))
      val sut = createRepositoryWith(List(ClaimedExpenseSheet(id, employee, expenses)), List(employee))

      run(sut.get(id)) should matchPattern {
        case Some(ClaimedExpenseSheet(`id`, `employee`, `expenses`)) =>
      }
    }
  }
  describe("save") {
    it("should save when employee exists") {
      val employee = Employee(UUID.randomUUID(), "A", "V")
      val sut = createRepositoryWith(List(), List(employee))
      val expenseSheet = OpenExpenseSheet(UUID.randomUUID(), employee, List())

      toBeDeletedExpenseSheetIds += expenseSheet.id

      run(for {
        _ <- sut.save(expenseSheet)
        es <- sut.get(expenseSheet.id)
      } yield es) should matchPattern {
        case Some(OpenExpenseSheet(_, _, _)) =>
      }
    }
  }

  override def afterEach(): Unit = {
    cleanUp(toBeDeletedExpenseSheetIds.toList, toBeDeletedEmployeeIds.toList)
  }

  def createRepositoryWith(expenseSheets: List[ExpenseSheet], employees: List[Employee] = List()): ExpenseSheetRepository[F]

  def run[A](toBeExecuted: F[A]) : A

  def cleanUp(expenseSheetIds: List[ExpenseSheetId], employeeIds: List[EmployeeId]): Unit
}
