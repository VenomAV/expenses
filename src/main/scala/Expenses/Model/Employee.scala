package Expenses.Model

import java.util.UUID

import Expenses.Model.Employee.EmployeeId
import Expenses.Utils.Validation
import scalaz.ValidationNel
import scalaz.Validation._
import scalaz.Scalaz._

sealed case class Employee private(id : EmployeeId, name: String, surname: String)

object Employee {
  type EmployeeId = UUID

  private val validateId = Validation.notNull[EmployeeId]("id is null")(_)
  private val validateName = Validation.notEmptyString("name is empty")(_)
  private val validateSurname = Validation.notEmptyString("surname is empty")(_)

  def create(id: EmployeeId, name: String, surname: String) : ValidationNel[String, Employee] =
    (validateId(id) |@|
      validateName(name) |@|
      validateSurname(surname))(new Employee(_, _, _))

  def create(name: String, surname: String) : ValidationNel[String, Employee] =
    create(UUID.randomUUID(), name, surname)
}