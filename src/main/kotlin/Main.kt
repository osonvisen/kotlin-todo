import java.sql.Connection
import java.sql.DriverManager


fun connect(): Connection = DriverManager.getConnection("jdbc:sqlite:todos.db")

fun main() {
    val repository = TodoRepository(connect())
    repository.createTable()

    TodoMenu(repository).run()
}
