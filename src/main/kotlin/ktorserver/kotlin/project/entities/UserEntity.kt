package ktorserver.kotlin.project.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ktorserver.kotlin.project.entities.PersonsTable.autoIncrement
import org.jetbrains.exposed.sql.Table

@Serializable
data class User(
    @SerialName("id") val id:Int=0,
    @SerialName("email") val email:String,
    @SerialName("password") val password:String
)

object UserTable: Table(){
    val ID = integer("ID").autoIncrement()
    val email = varchar("EMAIL",225).uniqueIndex()
    val password = varchar("PASSWORD",225)

    override val primaryKey = PrimaryKey(ID)
}