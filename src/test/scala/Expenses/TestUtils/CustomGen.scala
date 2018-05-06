package Expenses.TestUtils

import java.util.{Calendar, Date}

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
}
