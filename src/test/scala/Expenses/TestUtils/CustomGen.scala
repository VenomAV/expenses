package Expenses.TestUtils

import java.util.{Calendar, Date}

import Expenses.Model._
import Expenses.Utils.ErrorManagement.ValidationResult
import cats.data.Validated.{Invalid, Valid}
import org.scalacheck.Gen
import squants.market.Money

object CustomGen {
  def chooseCalendar(minMillis: Long = Long.MinValue, maxMillis: Long = Long.MaxValue): Gen[Calendar] =
    for {
      l <- Gen.chooseNum(minMillis, maxMillis)
      d = new Date(l)
      c = Calendar.getInstance()
    } yield {
      c.setTimeInMillis(d.getTime)
      c
    }

  val calendarInTheFuture : Gen[Calendar] = chooseCalendar(minMillis =  new Date().getTime + 1000)
  val calendarInThePast : Gen[Calendar] = chooseCalendar(maxMillis =  new Date().getTime)

  val nullOrEmptyString: Gen[String] = Gen.choose(0, 1) map {
    case 0 => null
    case _ => ""
  }

  val notNullOrEmptyString: Gen[String] = Gen.alphaNumStr suchThat(!_.isEmpty)

  def money(numGen: Gen[Double], currency: String) : Gen[Money] =
    for {
      value <- numGen
    } yield {
      Money.apply(value, currency)
    }

  def strictlyPositiveMoney(currency: String): Gen[Money] =
    money(Gen.chooseNum[Double](Double.MinPositiveValue, Double.MaxValue), currency)

  def positiveMoney(currency: String): Gen[Money] =
    money(Gen.chooseNum[Double](0, Double.MaxValue), currency)

  def negativeMoney(currency: String): Gen[Money] =
    money(Gen.chooseNum[Double](Double.MinValue, -Double.MinPositiveValue), currency)

  def zeroMoney(currency: String): Gen[Money] =
    money(Gen.chooseNum[Double](0.0, 0.0), currency)

  def sentence(minWordCount: Int = 0, maxWordCount: Int = Int.MaxValue) : Gen[String] =
    for {
      n <- Gen.choose(minWordCount, maxWordCount)
      list <- Gen.listOfN(n, notNullOrEmptyString)
    } yield {
      list.mkString(" ")
    }

  private def extractValid[T](result: ValidationResult[T]): T = result match {
    case Valid(x) => x
    case Invalid(_) => throw new Exception("Ops this should not happen")
  }

  val employee : Gen[Employee] =
    for {
      name <- notNullOrEmptyString
      surname <- notNullOrEmptyString
    } yield {
      extractValid(Employee.create(name, surname))
    }

  val expense : Gen[Expense] =
    for {
      calendar <- CustomGen.calendarInThePast
      cost <- CustomGen.money(Gen.chooseNum[Double](Double.MinPositiveValue, 49.99), "EUR")
    } yield {
      extractValid(Expense.createFood(cost, calendar.getTime))
    }

  val openExpenseSheet : Gen[OpenExpenseSheet] =
    for {
      employee <- CustomGen.employee
      expenses <- Gen.listOf(CustomGen.expense)
    } yield {
      extractValid(ExpenseSheet.createOpen(employee, expenses))
    }
}
