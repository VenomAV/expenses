package Infrastructure.Repositories

import Expenses.Model.ExpenseSheet.ExpenseSheetId
import Expenses.Model._
import Expenses.Repositories.ExpenseSheetRepository
import Infrastructure.Repositories.Doobie.implicits._
import cats.effect.IO
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.transactor.Transactor.Aux

class DoobieExpenseSheetRepository(implicit xa: Aux[IO, Unit]) extends ExpenseSheetRepository[IO] {

  type ExpenseSheetType = String
  type DBTuple = (ExpenseSheetId, ExpenseSheetType, List[Expense], Employee)

  override def get(id: ExpenseSheetId): IO[Option[ExpenseSheet]] =
    sql"""select es.id, es.type, es.expenses, e.id, e.name, e.surname
          from expensesheets es
          join employees e on e.id = es.employeeid
          where es.id=$id"""
      .query[DBTuple]
      .option
      .map(_.map(unsafeDBTupleToExpenseSheet))
      .transact(xa)

  override def save(expenseSheet: ExpenseSheet): IO[Unit] =
    sql"""insert into expensesheets (id, type, employeeid, expenses)
          values (${expenseSheet.id}, ${expenseSheetType(expenseSheet)}, ${expenseSheet.employee.id}, ${expenseSheet.expenses})"""
      .update.run.map(_ => ()).transact(xa)

  private def unsafeDBTupleToExpenseSheet(tuple: DBTuple) : ExpenseSheet = {
    val (id, expenseSheetType, expenses, employee) = tuple

    expenseSheetType match {
      case "O" => OpenExpenseSheet(id, employee, expenses)
      case "C" => ClaimedExpenseSheet(id, employee, expenses)
      case _ => throw new UnsupportedOperationException
    }
  }

  private def expenseSheetType(expenseSheet: ExpenseSheet) : ExpenseSheetType = expenseSheet match {
    case OpenExpenseSheet(_, _, _) => "O"
    case ClaimedExpenseSheet(_, _, _) => "C"
    case _ => throw new UnsupportedOperationException
  }
}
