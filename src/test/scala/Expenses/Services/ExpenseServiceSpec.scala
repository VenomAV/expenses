package Expenses.Services

import Expenses.Model.OpenExpenseSheet
import Expenses.TestUtils.CustomGen
import org.scalacheck.Properties
import org.scalacheck.Prop.forAll

//TODO da finire
//object ExpenseServiceSpec {
//  class ExpenseServiceLaws[F[_] : Applicative[F]](service: ExpenseService[F])
//  extends Properties("ExpenseService") {
//    property("") = forAll(CustomGen.employee) {
//      employee => service.openFor(employee).map {
//        case sheet: OpenExpenseSheet => sheet.employee == employee
//        case _ => false
//      }
//    }
//  }
//}
