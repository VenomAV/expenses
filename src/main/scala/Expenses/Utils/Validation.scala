package Expenses.Utils

import java.util.{Calendar, Date}

import cats.data.ValidatedNel
import cats.implicits._

object Validation {
  type Result[A] = ValidatedNel[String, A]

  def notEmptyString(errorMessage: String)(value: String): Result[String] =
    if (value == null || "".equals(value))
      errorMessage.invalidNel
    else
      value.validNel

  def dateInThePastOrToday(errorMessage: String)(date: Date): Result[Date] = {
    if (date == null || date.after(Calendar.getInstance.getTime))
      errorMessage.invalidNel
    else
      date.validNel
  }

  def notNull[T](errorMessage: String)(value: T) : Result[T] = {
    if (value == null)
      errorMessage.invalidNel
    else
      value.validNel
  }

  def nonEmptyList[T](errorMessage: String)(list: List[T]): Result[List[T]] =
    list match {
      case List(_, _) => list.validNel
      case _ => errorMessage.invalidNel
    }

  def valid[T](value: T): Result[T] = value.validNel
}
