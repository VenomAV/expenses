package Expenses.Model

import java.util.UUID

import Expenses.Model.Employee.EmployeeId
import Expenses.Utils.Validation
import scalaz.ValidationNel
import scalaz.Validation._
import scalaz.Scalaz._

/*
Employee should always have name and surname
 */
sealed case class Employee private(id : EmployeeId, name: String, surname: String)

object Employee {
  type EmployeeId = UUID

  private val validateName = Validation.notEmptyString("name is empty")(_)
  private val validateSurname = Validation.notEmptyString("surname is empty")(_)

  def create(id: EmployeeId, name: String, surname: String) : ValidationNel[String, Employee] =
    (validateName(name) |@| validateSurname(surname))(uncheckedCreate(id))

  def create(name: String, surname: String) : ValidationNel[String, Employee] =
    create(UUID.randomUUID(), name, surname)

  private def uncheckedCreate(id: EmployeeId)(name: String, surname: String) : Employee =
    new Employee(id, name, surname)
}