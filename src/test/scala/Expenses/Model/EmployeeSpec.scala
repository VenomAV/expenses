package Expenses.Model

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll
import org.scalacheck.Gen
import scalaz.{Failure, NonEmptyList, Success}

object EmployeeShouldAlwaysHaveNameAndSurname extends Properties("Employee should always have name and surname") {
  property("create with empty surname returns error") =
    forAll(Gen.alphaStr suchThat(!_.isEmpty)) {
      name => Employee.create(name, "") match {
        case Failure(NonEmptyList(head, _)) if "surname is empty".equals(head) => true
        case _ => false
      }
    }

  property("create with empty name returns error") =
    forAll(Gen.alphaStr suchThat(!_.isEmpty)) {
      surname => Employee.create("", surname) match {
        case Failure(NonEmptyList(head, _)) if "name is empty".equals(head) => true
        case _ => false
      }
    }

  property("create with not-empty name and surname returns Employee") =
    forAll(Gen.alphaStr suchThat(!_.isEmpty), Gen.alphaStr suchThat(!_.isEmpty)) {
      (name, surname) => Employee.create(name, surname) match {
        case Success(employee) => name.equals(employee.name) && surname.equals(employee.surname)
        case _ => false
      }
    }
}
