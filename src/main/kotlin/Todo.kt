import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement

data class Todo(val title: String, val done: Boolean = false, val id: Int? = null)

fun connect(): Connection = DriverManager.getConnection("jdbc:sqlite:todos.db")

fun createTable(connection: Connection) {
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

fun loadTodosFromDatabase(connection: Connection): MutableList<Todo> {
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

fun addTodo(connection: Connection, title: String):Todo {
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

fun printTodos(todos: List<Todo>){
    for (todo in todos) {
        val checkbox = if (todo.done) "[V]" else "[ ]"
        println("$checkbox ${todo.title}")
    }
}

fun saveTodoToDatabase(connection: Connection, todos: MutableList<Todo>){
    print("Legg til et gjøremål: ")
    val title = readln()

    if (title.isNotBlank()){
        todos.add(addTodo(connection, title))
    }
}

fun markTodoAsDone(connection: Connection, id: Int){
    connection.prepareStatement("UPDATE todo SET done = 1 WHERE id = ?").use { statement ->
        statement.setInt(1, id)
        statement.executeUpdate()
    }
}

fun completeTodo(connection: Connection, todos: MutableList<Todo>){
    var counter = 1
    for (todo in todos){
        if (todo.done){ // Hvis uttrykket er sant, altså at todo.done == true.
            counter++
            continue
        }
        println("${counter}. ${todo.title}")
        counter++
    }

    print("Hvilket nummer er ferdig? ")
    val finished = readln().trim().toIntOrNull()
    if (finished == null || finished !in 1..todos.size){
        print("Ugyldig tall.")
        return
    }

    val index = finished - 1
    val todo = todos[index]
    val id = checkNotNull(todo.id)

    markTodoAsDone(connection, id)
    todos[index] = todo.copy(done = true)
    println("Ferdig: ${todo.title}")
}

fun main() {
    val connection = connect()
    createTable(connection)

    val todos = loadTodosFromDatabase(connection)

    while (true){ // Starter menyen
        println()
        println("1. Vis listen")
        println("2. Legg til et gjøremål")
        println("3. Ferdigstille et gjøremål")
        println()
        println("0. Avslutt")
        print("Velg et tall: ")

        when (readln().trim()) {
            "1" -> printTodos(todos)
            "2" -> saveTodoToDatabase(connection, todos)
            "3" -> completeTodo(connection, todos)
            "0" -> {
                println("Hade bra!")
                return
            }
            else -> println("Ugyldig valg. Prøv igjen.")
        }
    }
}
