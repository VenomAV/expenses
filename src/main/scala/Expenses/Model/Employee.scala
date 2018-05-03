package Expenses.Model

import java.util.UUID

import Expenses.Model.Employee.EmployeeId

import scalaz.ValidationNel
import scalaz.Validation._
import scalaz.Scalaz._

/*
Employee should always have name and surname
 */
sealed class Employee private(val id : EmployeeId, val name: String, val surname: String)

object Employee {
  type EmployeeId = UUID

  private val validateName = notEmptyString("name is empty")(_)
  private val validateSurname = notEmptyString("surname is empty")(_)

  def create(id: EmployeeId, name: String, surname: String) : ValidationNel[String, Employee] =
    (validateName(name) |@| validateSurname(surname))(uncheckedCreate(id))

  def create(name: String, surname: String) : ValidationNel[String, Employee] =
    create(UUID.randomUUID(), name, surname)

  private def uncheckedCreate(id: EmployeeId)(name: String, surname: String) : Employee =
    new Employee(id, name, surname)

  private def notEmptyString(errorMessage: String)(value: String): ValidationNel[String, String] =
    if (value == null || "".equals(value)) errorMessage.failureNel else value.successNel
}