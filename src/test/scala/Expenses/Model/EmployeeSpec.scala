package Expenses.Model

import cats.data.NonEmptyList
import cats.data.Validated.{Invalid, Valid}
import org.scalacheck.Properties
import org.scalacheck.Prop.forAll
import org.scalacheck.Gen

object EmployeeSpec extends Properties("Employee") {
  property("surname should not be empty") =
    forAll(Gen.alphaStr suchThat(!_.isEmpty)) {
      name => Employee.create(name, "") match {
        case Invalid(NonEmptyList(head, _)) if "surname is empty".equals(head) => true
        case _ => false
      }
    }

  property("name should not be empty") =
    forAll(Gen.alphaStr suchThat(!_.isEmpty)) {
      surname => Employee.create("", surname) match {
        case Invalid(NonEmptyList(head, _)) if "name is empty".equals(head) => true
        case _ => false
      }
    }

  property("create with not-empty name and surname returns Employee") =
    forAll(Gen.alphaStr suchThat(!_.isEmpty), Gen.alphaStr suchThat(!_.isEmpty)) {
      (name, surname) => Employee.create(name, surname) match {
        case Valid(Employee(_, n, s)) => n == name && s == surname
        case _ => false
      }
    }
}
