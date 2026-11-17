import java.sql.Connection
import java.sql.Statement

class TodoRepository(private val connection: Connection) {

    fun createTable() {
        connection.createStatement().use { statement ->
            statement.execute(
                """
                CREATE TABLE IF NOT EXISTS todo (
                    id      INTEGER PRIMARY KEY AUTOINCREMENT,
                    title   TEXT NOT NULL,
                    done    INTEGER NOT NULL DEFAULT 0
                )
            """.trimIndent() // sqlite har ikke boolean, så her lagrer vi 0 for false og 1 for true
            )
        }
    }

    fun findAll(): List<Todo> {
        val todos = mutableListOf<Todo>()
        connection.createStatement().use { statement ->
            statement.executeQuery("SELECT id, title, done FROM todo ORDER BY id").use { rows ->
                while (rows.next()) {
                    todos.add(
                        Todo(
                            title = rows.getString("title"),
                            done = rows.getInt("done") == 1,
                            id = rows.getInt("id"),
                        )
                    )
                }
            }
        }
        return todos
    }

    fun insert(title: String):Todo {
        connection.prepareStatement(
            "INSERT INTO todo (title) VALUES (?)",
            Statement.RETURN_GENERATED_KEYS,
        ).use { statement ->
            statement.setString(1, title)
            statement.executeUpdate()
            statement.generatedKeys.use { keys ->
                keys.next()
                return Todo(title = title, id = keys.getInt(1))
            }
        }
    }

    fun markTodoAsDone(id: Int){
        connection.prepareStatement("UPDATE todo SET done = 1 WHERE id = ?").use { statement ->
            statement.setInt(1, id)
            statement.executeUpdate()
        }
    }

    fun updateTodo(id: Int, title: String){
        connection.prepareStatement("UPDATE todo SET title = ? WHERE id = ?").use { statement ->
            statement.setString(1, title)   // Første placeholder
            statement.setInt(2, id)         // Andre placeholder
            statement.executeUpdate()                          // Utfør
        }
    }

    fun deleteTodo(id: Int){
        connection.prepareStatement("DELETE FROM todo WHERE id = ?").use { statement ->
            statement.setInt(1, id)
            statement.executeUpdate()
        }
    }
}