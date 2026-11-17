import java.sql.Connection
import java.sql.DriverManager


fun connect(): Connection = DriverManager.getConnection("jdbc:sqlite:todos.db")

fun printTodos(todos: List<Todo>){
    for (todo in todos) {
        val checkbox = if (todo.done) "[V]" else "[ ]"
        println("$checkbox ${todo.title}")
    }
}

fun addTodo(repository: TodoRepository, todos: MutableList<Todo>){
    print("Legg til et gjøremål: ")
    val title = readln()

    if (title.isNotBlank()){
        todos.add(repository.insert(title))
    }
}

fun removeTodo(repository: TodoRepository, todos: MutableList<Todo>){
    var counter = 1
    for (todo in todos){
        println("${counter}. ${todo.title}")
        counter++
    }

    print("Hvilket nummer vil du slette? ")
    val number = readln().trim().toIntOrNull()
    if (number == null || number !in 1..todos.size){
        print("Ugyldig tall.")
        return
    }

    val index = number - 1
    val todo = todos[index]
    val id = checkNotNull(todo.id)

    repository.deleteTodo(id)
    todos.removeAt(index)
    println("Slettet gjøremål ${todo.title}")
}

fun completeTodo(repository: TodoRepository, todos: MutableList<Todo>){
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

    repository.markTodoAsDone(id)
    todos[index] = todo.copy(done = true)
    println("Ferdig: ${todo.title}")
}

fun editTodo(repository: TodoRepository, todos: MutableList<Todo>){
    var counter = 1
    for (todo in todos){
        println("${counter}. ${todo.title}")
        counter++
    }
    print("Hvilket nummer vil du endre? ")
    val number = readln().trim().toIntOrNull()

    if (number == null || number !in 1..todos.size){
        print("Ugyldig tall.")
        return
    }

    print("Hva vil du endre den til? ")
    val edit = readln().trim()

    if (!edit.isNotBlank()){
        print("Ingen endringer gjort!")
        return
    }

    val index = number - 1
    val todo = todos[index]
    val id = checkNotNull(todo.id)

    repository.updateTodo(id, edit)
    todos[index] = todo.copy(title = edit)
    println("Endret gjøremål ${todo.title} til ${edit}")
}

fun main() {
    val repository = TodoRepository(connect())
    repository.createTable()

    val todos = repository.findAll().toMutableList()

    while (true){ // Starter menyen
        println()
        println("1. Vis listen")
        println("2. Legg til et gjøremål")
        println("3. Ferdigstille et gjøremål")
        println("4. Slette et gjøremål")
        println("5. Endre et gjøremål")
        println()
        println("0. Avslutt")
        print("Velg et tall: ")

        when (readln().trim()) {
            "1" -> printTodos(todos)
            "2" -> addTodo(repository, todos)
            "3" -> completeTodo(repository, todos)
            "4" -> removeTodo(repository, todos)
            "5" -> editTodo(repository, todos)
            "0" -> {
                println("Hade bra!")
                return
            }
            else -> println("Ugyldig valg. Prøv igjen.")
        }
    }
}
