package Expenses.TestUtils

import Expenses.Model.ExpenseSheet
import Expenses.Model.ExpenseSheet.ExpenseSheetId
import Expenses.Repositories.ExpenseSheetRepository
import Expenses.TestUtils.AcceptanceTestUtils.Test
import cats.data.State

class InMemoryExpenseSheetRepository extends ExpenseSheetRepository[Test]{

  override def get(id: ExpenseSheetId): Test[Option[ExpenseSheet]] = ???

  override def save(expenseSheet: ExpenseSheet): Test[Unit] =
    State {
      state => (state.copy(expenseSheets = expenseSheet :: state.expenseSheets), ())
    }
}
