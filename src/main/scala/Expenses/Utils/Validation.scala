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
}
