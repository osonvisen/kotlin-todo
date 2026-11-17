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

In IntelliJ, open the project, open `Main.kt` and click the green arrow next to `fun main()`.

From the terminal:

```bash
./gradlew run
```

The database file `todos.db` is created in the project folder the first time you start the program. It is not committed to Git.

> **Note:** On Java 24 and newer you may see a warning about a *restricted method* being called by `org.sqlite.SQLiteJDBCLoader`. The SQLite driver uses native code, and newer Java versions warn about it. It is harmless. To hide it, add the JVM option `--enable-native-access=ALL-UNNAMED`.

## How it is built

The code is split into four files, each with one responsibility:

```
src/main/kotlin/
├── Todo.kt             # The data: one todo
├── TodoRepository.kt   # All SQL: reading and saving todos
├── TodoMenu.kt         # Everything the user sees: the menu, input and output
└── Main.kt             # Connects the parts and starts the menu
```

| File | Part | What it does |
|---|---|---|
| `Todo.kt` | `data class Todo` | One todo: a title, whether it is done, and its id from the database |
| `TodoRepository.kt` | `createTable()` | Creates the `todo` table if it does not exist |
| | `findAll()` | Reads all todos, not-done first, then in the order they were created |
| | `insert()` | Saves a new todo and gets back the id the database gave it |
| | `markTodoAsDone()` | Marks a todo as done |
| | `updateTodo()` | Changes the title of a todo |
| | `deleteTodo()` | Deletes a todo |
| `TodoMenu.kt` | `run()` | The menu, running in a loop until you choose `0` |
| | `addTodo()`, `completeTodo()`, `editTodo()`, `removeTodo()` | Ask the user what to do and call the repository |
| `Main.kt` | `main()` | Opens the database, creates the repository and the menu, and starts it |

`TodoRepository` gets the database connection through its constructor, and `TodoMenu` gets the repository through its constructor. The menu never sees SQL, and the repository never prints anything.

### Design decision: the database decides the order

`findAll()` sorts the todos in SQL with `ORDER BY done, id`, so the todos that are not done come first.

The menu keeps a list of todos in memory, so that it can show them with numbers. After every change (add, complete, edit or delete), the menu does not update that list itself. It **reloads the whole list from the database** instead.

Why:

- **One source of truth.** The list on screen always matches what is stored, and it is always sorted the same way. If a todo is marked as done, it moves down the list right away.
- **The numbers always match.** The numbers the user picks from are positions in the current list. Because the list is rebuilt after every change, a number can never point to the wrong todo.
- **Simpler code.** The menu does not need to add, replace or remove items in the list by hand.

The cost is one extra database query after each change. With a few todos in a local SQLite file, that is not noticeable. It is also how a web API usually works: each request reads fresh data from the database.

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
| `class TodoRepository(private val connection: Connection)` | Primary constructor: `class TodoRepository(SqlConnection connection)` |
| `private fun` / `private val` | `private` |
| `Int?` and `checkNotNull(x)` | `int?` and `x ?? throw ...` |
| `.use { }` | `using` |
| JDBC: `prepareStatement`, `executeQuery`, `executeUpdate` | ADO.NET: `SqlCommand`, `ExecuteReader`, `ExecuteNonQuery` |

<!-- Add your own thoughts here: what surprised you, what you liked better or worse than in C#. -->

## Ideas for later

- [x] Delete a todo
- [x] Edit a todo
- [x] Move the database code into its own `TodoRepository` class
- [x] Split the app into data, repository, menu and `main`
- [ ] Tests
- [ ] Turn it into a small REST API with Spring Boot

## About me

Ørjan Sønvisen, full-stack developer with a background in C#/.NET and React/TypeScript.
[LinkedIn](https://www.linkedin.com/in/osonvisen/)
