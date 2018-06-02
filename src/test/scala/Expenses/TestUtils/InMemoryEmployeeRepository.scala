package Expenses.TestUtils

import Expenses.Model.Employee
import Expenses.Model.Employee.EmployeeId
import Expenses.Repositories.EmployeeRepository
import cats.Id

class InMemoryEmployeeRepository extends EmployeeRepository[Id] {
  override def get(id: EmployeeId): Id[Option[Employee]] =
    Employee.create(id, "Andrea", "Vallotti")
      .map(Some(_))
      .getOrElse(None)
}

class InMemoryMissingEmployeeRepository extends EmployeeRepository[Id] {
  override def get(id: EmployeeId): Id[Option[Employee]] = None
}
