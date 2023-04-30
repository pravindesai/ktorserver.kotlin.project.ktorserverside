package ktorserver.kotlin.project.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import ktorserver.kotlin.project.db.DatabaseFactory
import ktorserver.kotlin.project.db.PersonDaoImpl
import ktorserver.kotlin.project.db.UserDaoImpl
import ktorserver.kotlin.project.entities.Person
import ktorserver.kotlin.project.entities.User
import ktorserver.kotlin.project.models.Customer
import ktorserver.kotlin.project.models.LoginInfo
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException
import java.io.File

fun Application.BaseRouting() {
    routing {
            Customer()
            Address()
            Files()
            DBRouting()
            Tokenization()
    }
}

fun Routing.Customer(){
    get("/customer"){
        val data = call.request
        println("URI: ${data.uri}")
        println("URI: ${data.headers.names()}")
        println("URI: ${data.queryParameters.names()}")
        println("URI: ${data.cookies.rawCookies.keys}")
        call.respondText("List of customers ${data.headers.get("HEADER1")}")

    }

    get ("/customers/{page}"){
        val params = call.parameters
        val pageNumber = params.get("page")
        call.respondText("Customer list page ${pageNumber}")
    }

    get("/customer/login") {
        val loginInfo = call.receive<String>()
        val loginInfoObj = Json.decodeFromString<LoginInfo>(loginInfo)
//        call.respondText("Login ${loginInfoObj}")

        val customer = Customer(
            id = (1..1000).random(),
            name = loginInfoObj.email,
            password = loginInfoObj.password
        )

        call.response.headers.append("return-token", "${(1..10000).random()}")

        call.respond(status = HttpStatusCode.OK,customer)

    }
}

fun Routing.Address(){
    get("/address") {
        call.respondText("Create a new Address")
    }
}

fun Routing.Files(){
    get("/image") {
        val file = File("files/ktor.png")
        call.response.header(
            HttpHeaders.ContentDisposition,
            ContentDisposition.Attachment.withParameter(
                ContentDisposition.Parameters.FileName, "ktor.png"
            ).toString()
        )
        call.respond(HttpStatusCode.OK)
    }
}

fun Routing.DBRouting(){
    val personDao = PersonDaoImpl()

    get("/allperson"){
        try{
            val allPerson = DatabaseFactory.forResponse(personDao.allPersons())
            call.respondText("$allPerson", status = HttpStatusCode.OK )
        }catch (e:Exception){
            call.respondText("FAILED: "+e.message.toString(), status = HttpStatusCode.ExpectationFailed )
        }
    }

    post("/addperson"){
        try{
            val data = call.receive<String>()
            val person = Json.decodeFromString<Person>(data)
            val result = personDao.addNewPerson(person)
            val response = DatabaseFactory.forResponse(result)
            call.respondText(response, status = HttpStatusCode.OK )
        }catch (e:Exception){
            call.respondText("FAILED: "+e.message.toString(), status = HttpStatusCode.ExpectationFailed )
        }
    }
    put("/editperson"){
        try{
            val data = call.receive<String>()
            val person = Json.decodeFromString<Person>(data)
            val result = personDao.editPerson(person)
            call.respondText("$result", status = HttpStatusCode.OK )
        }catch (e:Exception){
            call.respondText("FAILED: "+e.message.toString(), status = HttpStatusCode.ExpectationFailed )
        }
    }
    get("/person/{id}"){
        try{
            val params = call.parameters
            val id = params.get("id")
            id?.toIntOrNull()?.let {
                val result = personDao.getPerson(it)
                call.respondText("${result}", status = HttpStatusCode.OK )
            }?: kotlin.run {
                call.respondText("ID NOT FIND", status = HttpStatusCode.NotFound )
            }
        }catch (e:Exception){
            call.respondText("FAILED: "+e.message.toString(), status = HttpStatusCode.ExpectationFailed )
        }
    }

}

fun Routing.Tokenization(){

    val userDao = UserDaoImpl()
    get("/alluser"){
        try{
            val allPerson = DatabaseFactory.forResponse(userDao.allUsers())
            call.respondText(allPerson, status = HttpStatusCode.OK )
        }catch (e:Exception){
            call.respondText("FAILED: "+e.message.toString(), status = HttpStatusCode.ExpectationFailed )
        }
    }
    post("/adduser"){
        try{
            val data = call.receive<String>()
            val user = Json.decodeFromString<User>(data)
            val result:User? = userDao.addNewUser(user)
            val responseEncoded = DatabaseFactory.forResponse(result).encodeBase64().encodeBase64().reversed()
            call.respondText("token -> $responseEncoded", status = HttpStatusCode.OK )
        }catch (e:Exception){
            when(e.cause){
                is JdbcSQLIntegrityConstraintViolationException->{
                    call.respondText("Account already exits", status = HttpStatusCode.Conflict )
                }
                else ->{
                    call.respondText("FAILED: "+e.message.toString(), status = HttpStatusCode.ExpectationFailed )
                }
            }
        }
    }
    put("/edituser"){
        try{
            val data = call.receive<String>()
            val user = Json.decodeFromString<User>(data)
            val result = userDao.editUser(user)
            call.respondText("$result", status = HttpStatusCode.OK )
        }catch (e:Exception){
            call.respondText("FAILED: "+e.message.toString(), status = HttpStatusCode.ExpectationFailed )
        }
    }
    get("/user/{id}"){
        try{
            val params = call.parameters
            val id = params.get("id")
            id?.toIntOrNull()?.let {
                val result = userDao.getUser(it)
                call.respondText("${result}", status = HttpStatusCode.OK )
            }?: kotlin.run {
                call.respondText("ID NOT FIND", status = HttpStatusCode.NotFound )
            }
        }catch (e:Exception){
            call.respondText("FAILED: "+e.message.toString(), status = HttpStatusCode.ExpectationFailed )
        }
    }
    get("/user"){
        try{
            val params = call.parameters
            val id = params.get("token")
            id?.toIntOrNull()?.let {
                val result = userDao.getUser(it)
                call.respondText("${result}", status = HttpStatusCode.OK )
            }?: kotlin.run {
                call.respondText("ID NOT FIND", status = HttpStatusCode.NotFound )
            }
        }catch (e:Exception){
            call.respondText("FAILED: "+e.message.toString(), status = HttpStatusCode.ExpectationFailed )
        }
    }

}