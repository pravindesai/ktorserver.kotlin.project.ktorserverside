package ktorserver.kotlin.project.entities
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*


@Serializable
data class Person(
    @SerialName("ID") val ID:Int,
    @SerialName("Name") val Name:String,
    @SerialName("Address") val Address:String,
    @SerialName("City") val City:String
)

object PersonsTable:Table(){
    val ID = integer("ID").autoIncrement()
    val Name = varchar("Name", 225)
    val Address = varchar("Address",225)
    val City = varchar("City",225)

    override val primaryKey = PrimaryKey(ID)
}