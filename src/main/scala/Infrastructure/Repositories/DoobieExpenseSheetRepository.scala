package Infrastructure.Repositories

import Expenses.Model.ExpenseSheet.ExpenseSheetId
import Expenses.Model._
import Expenses.Repositories.ExpenseSheetRepository
import cats.effect.IO
import cats.free.Free
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.transactor.Transactor.Aux
import Infrastructure.Repositories.Doobie.implicits._
import doobie.free.connection.ConnectionIO

class DoobieExpenseSheetRepository(implicit xa: Aux[IO, Unit]) extends ExpenseSheetRepository[IO] {

  type ExpenseSheetType = String
  type DBTuple = (ExpenseSheetId, ExpenseSheetType, List[Expense], Employee)

  override def get(id: ExpenseSheetId): IO[Option[ExpenseSheet]] =
    select(id)
      .map(_.map(unsafeDBTupleToExpenseSheet))
      .transact(xa)

  private def select(id: ExpenseSheetId): ConnectionIO[Option[DBTuple]] =
    sql"""select es.id, es.type, es.expenses, e.id, e.name, e.surname
          from expensesheets es
          join employees e on e.id = es.employeeid
          where es.id=$id"""
      .query[DBTuple]
      .option

  private def unsafeDBTupleToExpenseSheet(tuple: DBTuple) : ExpenseSheet = {
    val (id, expenseSheetType, expenses, employee) = tuple

    expenseSheetType match {
      case "O" => OpenExpenseSheet(id, employee, expenses)
      case "C" => ClaimedExpenseSheet(id, employee, expenses)
      case _ => throw new UnsupportedOperationException
    }
  }

  override def save(expenseSheet: ExpenseSheet): IO[Unit] =
    (for {
      exists <- employeeExists(expenseSheet.employee)
      _ <- exists
        .map(_ => insert(expenseSheet))
        .getOrElse(Free.pure(0))
    } yield ()).transact(xa)

  private def employeeExists(employee: Employee): ConnectionIO[Option[Boolean]] =
    sql"select 1 from employees where id=${employee.id}".query[Boolean].option

  private def insert(expenseSheet: ExpenseSheet): ConnectionIO[Int] =
    sql"""insert into expensesheets (id, type, employeeid, expenses)
          values (${expenseSheet.id}, ${expenseSheetType(expenseSheet)},
            ${expenseSheet.employee.id}, ${expenseSheet.expenses})"""
    .update.run

  private def expenseSheetType(expenseSheet: ExpenseSheet) : ExpenseSheetType = expenseSheet match {
    case OpenExpenseSheet(_, _, _) => "O"
    case ClaimedExpenseSheet(_, _, _) => "C"
    case _ => throw new UnsupportedOperationException
  }
}
