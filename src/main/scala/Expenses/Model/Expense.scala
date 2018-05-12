package Expenses.Model

import java.util.Date

import Expenses.Utils.Validation
import squants.market.Money
import cats.implicits._

sealed trait Expense {
  def cost: Money
  def date: Date
}

case class TravelExpense private (cost: Money, date: Date, from: String, to: String) extends Expense
case class FoodExpense private (cost: Money, date: Date) extends Expense
case class AccommodationExpense private (cost: Money, date: Date, hotel: String) extends Expense
case class OtherExpense private (cost: Money, date: Date, description: String) extends  Expense

object Expense {
  val validateDate: Date => Validation.Result[Date] =
    Validation.dateInThePastOrToday("date cannot be in the future")(_)

  def validateCost(cost: Money): Validation.Result[Money] =
    if (cost.amount <= 0)
      "cost is less or equal to zero".invalidNel
  else
      cost.validNel
}

object TravelExpense {
  private val validateTo = Validation.notEmptyString("to is null or empty")(_)
  private val validateFrom = Validation.notEmptyString("from is null or empty")(_)

  def create(cost: Money, date: Date, from: String, to: String): Validation.Result[TravelExpense] =
    (Expense.validateCost(cost),
      Expense.validateDate(date),
      validateFrom(from),
      validateTo(to)).mapN(new TravelExpense(_, _, _, _))
}

object FoodExpense {
  private def maxCostLimitValidation(cost: Money): Validation.Result[Money] =
    if (cost.amount >= 50)
      "cost is greater than or equal to 50".invalidNel
  else
      cost.validNel

  def create(cost: Money, date: Date): Validation.Result[FoodExpense] =
    (Expense.validateCost(cost),
      Expense.validateDate(date),
      maxCostLimitValidation(cost)).mapN((c, d, _) => new FoodExpense(c, d))
}

object AccommodationExpense {
  private val validateHotel = Validation.notEmptyString("hotel is null or empty")(_)

  def create(cost: Money, date: Date, hotel: String): Validation.Result[AccommodationExpense] =
    (Expense.validateCost(cost),
      Expense.validateDate(date),
      validateHotel(hotel)).mapN(new AccommodationExpense(_, _, _))
}

object OtherExpense {
  private def countWords(description: String): Int = {
    description.split(" ").map(_.trim).count(!_.isEmpty)
  }

  private def validateDescription(description: String): Validation.Result[String] =
    if (countWords(description) < 10)
      "description contains less than 10 words".invalidNel
    else
      description.validNel

  def create(cost: Money, date: Date, description: String): Validation.Result[OtherExpense] =
    (Expense.validateCost(cost),
      Expense.validateDate(date),
      validateDescription(description)).mapN(new OtherExpense(_, _, _))
}
