class TodoMenu (private val repository: TodoRepository) {

    private var todos = repository.findAll().toMutableList()

    private fun reload(){
        todos = repository.findAll().toMutableList()
    }

    fun run() {
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
                "1" -> printTodos()
                "2" -> addTodo()
                "3" -> completeTodo()
                "4" -> removeTodo()
                "5" -> editTodo()
                "0" -> {
                    println("Hade bra!")
                    return
                }
                else -> println("Ugyldig valg. Prøv igjen.")
            }
        }
    }

    private fun printTodos(){
        for (todo in todos) {
            val checkbox = if (todo.done) "[V]" else "[ ]"
            println("$checkbox ${todo.title}")
        }
    }

    private fun addTodo(){
        print("Legg til et gjøremål: ")
        val title = readln()

        if (title.isNotBlank()){
            todos.add(repository.insert(title))
        }
        reload()
    }

    private fun removeTodo(){
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
        reload()
    }

    private fun completeTodo(){
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
        reload()
    }

    private fun editTodo(){
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
        reload()
    }
}