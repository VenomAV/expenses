package Expenses.TestUtils

import Expenses.Model.ExpenseSheet
import Expenses.Model.ExpenseSheet.ExpenseSheetId
import Expenses.Repositories.ExpenseSheetRepository
import cats.Id

class InMemoryExpenseSheetRepository extends ExpenseSheetRepository[Id]{

  var savedExpenseSheet : Option[ExpenseSheet] = None

  override def get(id: ExpenseSheetId): Id[ExpenseSheet] = ???

  override def save(expenseSheet: ExpenseSheet): Id[Unit] = savedExpenseSheet = Some(expenseSheet)
}
