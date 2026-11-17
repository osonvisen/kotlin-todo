# Kotlin Todo

A small command-line todo app written in **Kotlin**, with the todos stored in a **SQLite** database.

This is a learning project. I work with C#/.NET and built this to learn Kotlin step by step, from a list in memory to a menu-driven program that saves its data. The code is deliberately simple, so that every line is easy to follow.

## What it does

```
1. Vis listen
2. Legg til et gjøremål
3. Ferdigstille et gjøremål
4. Slette et gjøremål
5. Endre et gjøremål

0. Avslutt
Velg et tall: 1

[ ] Kjøpe melk
[V] Høgge ved
[ ] Lære Kotlin
```

- **Show** all todos, with `[V]` for the ones that are done
- **Add** a new todo (new todos are not done by default)
- **Mark** a todo as done
- **Delete** a todo
- **Edit** the title of a todo
- Todos are **saved** in `todos.db`, so they are still there the next time you start the program

The menu text is in Norwegian.

## Getting started

### Prerequisites

| Tool | Version |
|---|---|
| JDK | 25 |
| IntelliJ IDEA | Recommended, but not required |

You do not need to install Gradle or SQLite. The project uses the Gradle Wrapper, and the SQLite driver includes the database engine.

### Run

In IntelliJ, open the project and click the green arrow next to `fun main()`.

From the terminal:

```bash
./gradlew run
```

The database file `todos.db` is created in the project folder the first time you start the program. It is not committed to Git.

> **Note:** On Java 24 and newer you may see a warning about a *restricted method* being called by `org.sqlite.SQLiteJDBCLoader`. The SQLite driver uses native code, and newer Java versions warn about it. It is harmless. To hide it, add the JVM option `--enable-native-access=ALL-UNNAMED`.

## How it is built

| Part | What it does |
|---|---|
| `data class Todo` | One todo: a title, whether it is done, and its id from the database |
| `connect()` / `createTable()` | Opens the database and creates the `todo` table if it does not exist |
| `loadTodosFromDatabase()` | Reads all todos when the program starts |
| `addTodo()` | Inserts a new todo and gets back the id the database gave it |
| `saveTodoToDatabase()` | Asks for a title and saves the new todo |
| `markTodoAsDone()` | Updates a todo in the database |
| `completeTodo()` | Asks which todo is done and saves it |
| `deleteTodo()` | Deletes a todo from the database |
| `removeTodo()` | Asks which todo to delete and removes it |
| `updateTodo()` | Changes the title of a todo in the database |
| `editTodo()` | Asks which todo to edit and for the new title |
| `main()` | The menu, running in a loop until you choose `0` |

The database has one table:

```sql
CREATE TABLE IF NOT EXISTS todo (
    id    INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT    NOT NULL,
    done  INTEGER NOT NULL DEFAULT 0   -- SQLite has no boolean: 0 = false, 1 = true
)
```

Together, these cover all four basic database operations, often called **CRUD**: create (`INSERT`), read (`SELECT`), update (`UPDATE`) and delete (`DELETE`).

All SQL uses parameters (`?`) instead of putting user input directly into the query, which protects against SQL injection.

## What I learned

How the Kotlin in this project compares to what I know from C#:

| Kotlin | C# |
|---|---|
| `data class Todo(val title: String, val done: Boolean = false)` | `record Todo(string Title, bool Done = false)` |
| `fun main()` outside any class | `static void Main()` |
| `listOf(...)` / `mutableListOf(...)` | `IReadOnlyList<T>` / `List<T>` |
| `for (todo in todos)` | `foreach (var todo in todos)` |
| `if (todo.done) "[V]" else "[ ]"` | `todo.Done ? "[V]" : "[ ]"` |
| `"$checkbox ${todo.title}"` | `$"{checkbox} {todo.Title}"` |
| `when (choice) { "1" -> ... else -> ... }` | `switch` expression |
| `readln()` / `print()` / `println()` | `Console.ReadLine()` / `Console.Write()` / `Console.WriteLine()` |
| `"42".toIntOrNull()` | `int.TryParse("42", out var number)` |
| `todo.copy(done = true)` / `todo.copy(title = newTitle)` | `todo with { Done = true }` / `todo with { Title = newTitle }` |
| `todos.removeAt(index)` | `todos.RemoveAt(index)` |
| `Int?` and `checkNotNull(x)` | `int?` and `x ?? throw ...` |
| `.use { }` | `using` |
| JDBC: `prepareStatement`, `executeQuery`, `executeUpdate` | ADO.NET: `SqlCommand`, `ExecuteReader`, `ExecuteNonQuery` |

<!-- Add your own thoughts here: what surprised you, what you liked better or worse than in C#. -->

## Ideas for later

- [x] Delete a todo
- [x] Edit a todo
- [ ] Move the database code into its own `TodoRepository` class
- [ ] Tests
- [ ] Turn it into a small REST API with Spring Boot

## About me

Ørjan Sønvisen, full-stack developer with a background in C#/.NET and React/TypeScript.
[LinkedIn](https://www.linkedin.com/in/osonvisen/)
