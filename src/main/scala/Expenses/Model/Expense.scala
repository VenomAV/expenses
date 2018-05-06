package Expenses.Model

import java.util.Date

import Expenses.Utils.Validation
import scalaz.Scalaz._
import scalaz.ValidationNel
import squants.market.Money

sealed trait Expense {
  def cost: Money
  def date: Date
}

case class TravelExpense private (cost: Money, date: Date, from: String, to: String) extends Expense
case class FoodExpense private (cost: Money, date: Date) extends Expense
case class AccommodationExpense private (cost: Money, date: Date, hotel: String) extends Expense
case class OtherExpense private (cost: Money, date: Date, description: String) extends  Expense

object Expense {
  private val validateDate =
    Validation.dateInThePastOrToday("date cannot be in the future")(_)

  private def validateCost(cost: Money): ValidationNel[String, Money] =
    if (cost.amount <= 0)
      "cost is less or equal to zero".failureNel
  else
      cost.successNel

  def validate(cost: Money, date: Date) = validateCost(cost) |@| validateDate(date)

}

object TravelExpense {
  private val validateTo = Validation.notEmptyString("to is null or empty")(_)
  private val validateFrom = Validation.notEmptyString("from is null or empty")(_)

  def create(cost: Money, date: Date, from: String, to: String): ValidationNel[String, TravelExpense] =
    (Expense.validate(cost, date) |@|
      validateFrom(from) |@|
      validateTo(to)) { new TravelExpense(_, _, _, _) }
}

object FoodExpense {
  private def maxCostLimitValidation(cost: Money): ValidationNel[String, Money] =
    if (cost.amount >= 50)
      "cost is greater than or equal to 50".failureNel
  else
      cost.successNel

  def create(cost: Money, date: Date): ValidationNel[String, FoodExpense] =
    (Expense.validate(cost, date) |@|
      maxCostLimitValidation(cost)){ (c, d, _) => new FoodExpense(c, d) }
}

object AccommodationExpense {
  private val validateHotel = Validation.notEmptyString("hotel is null or empty")(_)

  def create(cost: Money, date: Date, hotel: String): ValidationNel[String, AccommodationExpense] =
    (Expense.validate(cost, date) |@|
      validateHotel(hotel)) { new AccommodationExpense(_, _, _) }
}

object OtherExpense {
  private def countWords(description: String): Int = {
    description.split(" ").map(_.trim).count(!_.isEmpty)
  }

  private def validateDescription(description: String): ValidationNel[String, String] =
    if (countWords(description) < 10)
      "description contains less than 10 words".failureNel
    else
      description.successNel

  def create(cost: Money, date: Date, description: String): ValidationNel[String, OtherExpense] =
    (Expense.validate(cost, date) |@|
      validateDescription(description)) { new OtherExpense(_, _, _) }
}
