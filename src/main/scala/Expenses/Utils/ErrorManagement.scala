package Expenses.Utils

import java.util.{Calendar, Date}

import cats.data.ValidatedNel
import cats.implicits._

object ErrorManagement {
  type Error = String
  type ValidationResult[A] = ValidatedNel[Error, A]

  def notEmptyString(errorMessage: String)(value: String): ValidationResult[String] =
    if (value == null || "".equals(value))
      errorMessage.invalidNel
    else
      value.validNel

  def dateInThePastOrToday(errorMessage: String)(date: Date): ValidationResult[Date] = {
    if (date == null || date.after(Calendar.getInstance.getTime))
      errorMessage.invalidNel
    else
      date.validNel
  }

  def notNull[T](errorMessage: String)(value: T) : ValidationResult[T] = {
    if (value == null)
      errorMessage.invalidNel
    else
      value.validNel
  }

  def nonEmptyList[T](errorMessage: String)(list: List[T]): ValidationResult[List[T]] =
    list match {
      case _ :: _ => list.validNel
      case _ => errorMessage.invalidNel
    }
}
