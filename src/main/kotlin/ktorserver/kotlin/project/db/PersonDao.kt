package ktorserver.kotlin.project.db

import ktorserver.kotlin.project.db.DatabaseFactory.dbQuery
import ktorserver.kotlin.project.entities.Person
import ktorserver.kotlin.project.entities.PersonsTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

interface PersonDao {
    suspend fun allPersons(): List<Person>
    suspend fun getPerson(id: Int): Person?
    suspend fun addNewPerson(person: Person): Person?
    suspend fun editPerson(person: Person): Boolean
    suspend fun deletePerson(id: Int): Boolean
}

class PersonDaoImpl : PersonDao {

    private fun ResultRow.ToPerson() = Person(
        ID = this[PersonsTable.ID],
        Name = this[PersonsTable.Name],
        Address = this[PersonsTable.Address],
        City = this[PersonsTable.City]
    )

    override suspend fun allPersons(): List<Person> = dbQuery {
            PersonsTable.selectAll().map { it.ToPerson() }
        }

    override suspend fun getPerson(id: Int): Person? = dbQuery {
        PersonsTable.select { PersonsTable.ID eq id }.map { it.ToPerson() }.singleOrNull()
    }

    override suspend fun addNewPerson(person: Person): Person? = dbQuery {
        val insertStatement = PersonsTable.insert {
            it[ID] = person.ID
            it[Name] = person.Name
            it[Address] = person.Address
            it[City] = person.City
        }
        insertStatement.resultedValues?.singleOrNull()?.ToPerson()

    }

    override suspend fun editPerson(person: Person): Boolean = dbQuery {
        val insertStatement = PersonsTable.update({ PersonsTable.ID eq person.ID }) {
            it[Name] = person.Name
            it[Address] = person.Address
            it[City] = person.City
        }
        insertStatement==1
    }

    override suspend fun deletePerson(id: Int): Boolean = dbQuery {
        PersonsTable.deleteWhere {
            ID eq id
        } > 0
    }

}