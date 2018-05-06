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
  val validateDate: Date => ValidationNel[String, Date] =
    Validation.dateInThePastOrToday("date cannot be in the future")(_)
  def validateCost(cost: Money): ValidationNel[String, Money] =
    if (cost.amount <= 0)
      "cost is less or equal to zero".failureNel
  else
      cost.successNel
}

object TravelExpense {
  val validateTo: String => ValidationNel[String, String] =
    Validation.notEmptyString("to is null or empty")(_)
  val validateFrom: String => ValidationNel[String, String] =
    Validation.notEmptyString("from is null or empty")(_)

  def create(cost: Money, date: Date, from: String, to: String): ValidationNel[String, TravelExpense] =
    (Expense.validateCost(cost) |@|
      Expense.validateDate(date) |@|
      validateFrom(from) |@|
      validateTo(to))(new TravelExpense(_, _, _, _))
}

object FoodExpense {
  def validateCost(cost: Money): ValidationNel[String, Money] =
    if (cost.amount >= 50)
      "cost is greater than or equal to 50".failureNel
  else
      Expense.validateCost(cost)

  def create(cost: Money, date: Date): ValidationNel[String, FoodExpense] =
    (validateCost(cost) |@|
      Expense.validateDate(date))(new FoodExpense(_, _))
}

object AccommodationExpense {
  val validateHotel: String => ValidationNel[String, String] =
    Validation.notEmptyString("hotel is null or empty")(_)

  def create(cost: Money, date: Date, hotel: String): ValidationNel[String, AccommodationExpense] =
    (Expense.validateCost(cost) |@|
      Expense.validateDate(date) |@|
      validateHotel(hotel))(new AccommodationExpense(_, _, _))
}

object OtherExpense {
  private def countWords(description: String): Int = {
    description.split(" ").map(_.trim).count(!_.isEmpty)
  }

  def validateDescription(description: String): ValidationNel[String, String] =
    if (countWords(description) < 10)
      "description contains less than 10 words".failureNel
    else
      description.successNel

  def create(cost: Money, date: Date, description: String): ValidationNel[String, OtherExpense] =
    (Expense.validateCost(cost) |@|
      Expense.validateDate(date) |@|
      validateDescription(description))(new OtherExpense(_, _, _))
}
