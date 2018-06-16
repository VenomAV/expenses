package Expenses.Repositories

import Expenses.Model.ExpenseSheet
import Expenses.Model.ExpenseSheet.ExpenseSheetId

trait ExpenseSheetRepository[F[_]] {
  def get(id: ExpenseSheetId) : F[Option[ExpenseSheet]]
  def save(expenseSheet: ExpenseSheet) : F[Unit]
}
