package ktorserver.kotlin.project.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ktorserver.kotlin.project.entities.PersonsTable
import ktorserver.kotlin.project.entities.UserTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.ktorm.database.TransactionIsolation

object DatabaseFactory {
    var database:Database? = null
    fun init() {
        database = Database.connect(hikari())

        transaction(database) {
            SchemaUtils.create(PersonsTable)
            SchemaUtils.create(UserTable)
        }
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = "org.h2.Driver"
        config.jdbcUrl = "jdbc:h2:mem:test"
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.validate()
        return HikariDataSource(config)
    }


    suspend fun <T> dbQuery(block:  () -> T): T =
        newSuspendedTransaction(Dispatchers.IO,database){
            block()
        }

    inline fun <reified T> forResponse(data:T):String {
        return Json.encodeToString(data)
    }
}