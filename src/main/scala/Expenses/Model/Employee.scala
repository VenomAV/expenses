package Expenses.Model

import java.util.UUID

import Expenses.Model.Employee.EmployeeId
import Expenses.Utils.Validation
import cats.implicits._

sealed case class Employee private(id : EmployeeId, name: String, surname: String)

object Employee {
  type EmployeeId = UUID

  private val validateId = Validation.notNull[EmployeeId]("id is null")(_)
  private val validateName = Validation.notEmptyString("name is empty")(_)
  private val validateSurname = Validation.notEmptyString("surname is empty")(_)

  def create(id: EmployeeId, name: String, surname: String) : Validation.Result[Employee] =
    (validateId(id), validateName(name), validateSurname(surname))
      .mapN(new Employee(_, _, _))

  def create(name: String, surname: String) : Validation.Result[Employee] =
    create(UUID.randomUUID(), name, surname)
}