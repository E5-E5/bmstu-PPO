import org.example.`package`.exceptions.ConnectBDException
import org.example.`package`.postgres.password
import org.example.`package`.postgres.url
import org.example.`package`.postgres.user
import java.sql.Connection
import java.sql.DriverManager

class PostgresDBConnector() {
    private var connection: Connection? = null

    private fun connect() {
        try {
            connection = DriverManager.getConnection(url, user, password)
//            println("Successfully connected to PostgreSQL database.")
        } catch (e: Exception) {
//            println("Failed to connect to PostgreSQL database: ${e.message}")
            throw ConnectBDException()
        }
    }

    fun getConnection(): Connection? {
        connect()
        return connection
    }
}

fun main() {
    val url = "jdbc:postgresql://localhost:5432/diarybd"
    val user = "postgres"
    val password = "herogem2003"
//
//    val connector = PostgresDBConnector(url, user, password)
//    val connection = connector.getConnection()
//    try {
//        val statement = connection?.createStatement()
//        var res = statement?.executeQuery("Select * from school.subject")
//        res?.use {
//            while (res.next()) {
//                println(res.getString("namesubject"))
//
//                // Дальнейшая обработка данных
//                // Например, вывод на консоль или сохранение в структуры данных
//            }
//        }
//    } catch (e: Exception) {
//        println("Error executing query: ${e.message}")
//        null
//    }
//
//    // Используйте connection для ваших запросов к базе данных
//
//    connector.disconnect()
    var letter = "Б"
    var Number = 11
    val connector = PostgresDBConnector()
    val query = "INSERT INTO diary.class (ClassId, ClassLetter, ClassNumber) " +
            "VALUES (DEFAULT, '${letter}', ${Number}) "

    try {
        connector.getConnection()?.use { connection ->
            val statement = connection.createStatement()
            statement.executeUpdate(query)
        }
    } catch (e: Exception) {
        println("Error creating user: ${e.message}")
    }
}