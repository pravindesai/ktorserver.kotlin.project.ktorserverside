package ktorserver.kotlin.project.db

import io.ktor.util.*
import ktorserver.kotlin.project.entities.User
import ktorserver.kotlin.project.entities.UserTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

interface UserDAO {
    suspend fun allUsers(): List<User>
    suspend fun getUser(id: Int): User?
    suspend fun addNewUser(User: User): User?
    suspend fun editUser(User: User): Boolean
    suspend fun deleteUser(id: Int): Boolean
}

class UserDaoImpl : UserDAO {

    private fun ResultRow.ToUser() = User(
        id = this[UserTable.ID],
        email = this[UserTable.email],
        password = this[UserTable.password]
    )

    override suspend fun allUsers(): List<User> = DatabaseFactory.dbQuery {
        UserTable.selectAll().map { it.ToUser() }
    }

    override suspend fun getUser(id: Int): User? = DatabaseFactory.dbQuery {
        UserTable.select { UserTable.ID eq id }.map { it.ToUser() }.singleOrNull()
    }

    override suspend fun addNewUser(User: User): User? = DatabaseFactory.dbQuery {
        val insertStatement = UserTable.insert {
            it[email] = User.email
            it[password] = User.password.encodeBase64()
        }
        insertStatement.resultedValues?.singleOrNull()?.ToUser()
    }

    override suspend fun editUser(User: User): Boolean = DatabaseFactory.dbQuery {
        val insertStatement = UserTable.update({ UserTable.ID eq User.id }) {
            it[email] = User.email
            it[password] = User.password.encodeBase64()
        }
        insertStatement == 1
    }

    override suspend fun deleteUser(id: Int): Boolean = DatabaseFactory.dbQuery {
        UserTable.deleteWhere {
            ID eq id
        } > 0
    }
}