package Expenses.Model

import java.util.{Calendar, Date}

import scalaz.ValidationNel
import squants.market.Money
import scalaz.Scalaz._

/*
Expense date should always be in the past, or in the same day when it is created
TravelExpense from and to should be not empty strings
FoodExpense cost should be less than 50€
OtherExpense description should always be more than ten words
 */
sealed trait Expense {
  def cost: Money
  def date: Date
}

case class TravelExpense private (cost: Money, date: Date, from: String, to: String) extends Expense
case class FoodExpense private (cost: Money, date: Date) extends Expense
case class AccommodationExpense private (cost: Money, date: Date, hotel: String) extends Expense
case class OtherExpense private (cost: Money, date: Date, description: String) extends  Expense

object Expense {
  def travelExpense(cost: Money, date: Date, from: String, to: String): ValidationNel[String, TravelExpense] =
    (dateInThePastOrToday(date) |@| dateInThePastOrToday(date))((date, fake) => new TravelExpense(null, date, null, null))

  def foodExpense(cost: Money, date: Date): ValidationNel[String, FoodExpense] = ???
  def accommodationExpense(cost: Money, date: Date, hotel: String): ValidationNel[String, AccommodationExpense] = ???
  def otherExpense(cost: Money, date: Date, description: String): ValidationNel[String, OtherExpense] = ???

  private def dateInThePastOrToday(date: Date): ValidationNel[String, Date] = {
    if (date == null || date.after(Calendar.getInstance.getTime))
      "date cannot be in the future".failureNel
    else
      date.successNel
  }
}