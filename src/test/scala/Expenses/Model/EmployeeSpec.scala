package Expenses.Model

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll
import org.scalacheck.Gen
import scalaz.{Failure, NonEmptyList, Success}

object EmployeeSpec extends Properties("Employee") {
  property("surname should not be empty") =
    forAll(Gen.alphaStr suchThat(!_.isEmpty)) {
      name => Employee.create(name, "") match {
        case Failure(NonEmptyList(head, _)) if "surname is empty".equals(head) => true
        case _ => false
      }
    }

  property("name should not be empty") =
    forAll(Gen.alphaStr suchThat(!_.isEmpty)) {
      surname => Employee.create("", surname) match {
        case Failure(NonEmptyList(head, _)) if "name is empty".equals(head) => true
        case _ => false
      }
    }

  property("create with not-empty name and surname returns Employee") =
    forAll(Gen.alphaStr suchThat(!_.isEmpty), Gen.alphaStr suchThat(!_.isEmpty)) {
      (name, surname) => Employee.create(name, surname) match {
        case Success(Employee(_, n, s)) => n == name && s == surname
        case _ => false
      }
    }
}
