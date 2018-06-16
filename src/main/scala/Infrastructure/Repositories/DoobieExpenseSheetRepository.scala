package Infrastructure.Repositories

import Expenses.Model.ExpenseSheet.ExpenseSheetId
import Expenses.Model._
import Expenses.Repositories.ExpenseSheetRepository
import Infrastructure.JsonCodecs.implicits._
import Infrastructure.Repositories.Doobie._
import cats.effect.IO
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.meta.Meta
import doobie.util.transactor.Transactor.Aux

class DoobieExpenseSheetRepository(implicit xa: Aux[IO, Unit]) extends ExpenseSheetRepository[IO] {

  implicit val ExpenseListMeta: Meta[List[Expense]] = codecMeta[List[Expense]]

  type ExpenseSheetType = String
  type DBTuple = (ExpenseSheetId, ExpenseSheetType, List[Expense], Employee)

  override def get(id: ExpenseSheetId): IO[Option[ExpenseSheet]] =
    sql"""select es.id, es.type, es.expenses, e.id, e.name, e.surname
          from expensesheets es
          join employees e on e.id = es.employeeid
          where es.id=$id"""
      .query[DBTuple]
      .option
      .map(_.map(tupleToExpenseSheet))
      .transact(xa)

  override def save(expenseSheet: ExpenseSheet): IO[Unit] =
    sql"""insert into expensesheets (id, type, employeeid, expenses)
          values (${expenseSheet.id}, 'O', ${expenseSheet.employee.id}, ${expenseSheet.expenses})"""
      .update.run.map(_ => ()).transact(xa)

  def tupleToExpenseSheet(tuple: DBTuple) : ExpenseSheet = {
    val (id, expenseSheetType, expenses, employee) = tuple

    expenseSheetType match {
      case "O" => OpenExpenseSheet(id, employee, expenses)
      case _ => throw new UnsupportedOperationException
    }
  }
}
