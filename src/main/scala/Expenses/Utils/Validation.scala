package Expenses.Utils

import java.util.{Calendar, Date}

import scalaz.ValidationNel
import scalaz.Scalaz._

object Validation {
  def notEmptyString(errorMessage: String)(value: String): ValidationNel[String, String] =
    if (value == null || "".equals(value))
      errorMessage.failureNel
    else
      value.successNel

  def dateInThePastOrToday(errorMessage: String)(date: Date): ValidationNel[String, Date] = {
    if (date == null || date.after(Calendar.getInstance.getTime))
      errorMessage.failureNel
    else
      date.successNel
  }

  def notNull[T](errorMessage: String)(value: T) : ValidationNel[String, T] = {
    if (value == null)
      errorMessage.failureNel
    else
      value.successNel
  }

  def nonEmptyList[T](errorMessage: String)(list: List[T]): ValidationNel[String, List[T]] =
    list match {
      case List(_, _) => list.successNel
      case _ => errorMessage.failureNel
    }
}
