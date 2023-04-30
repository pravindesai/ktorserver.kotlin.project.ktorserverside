package ktorserver.kotlin.project.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.customerRoute() = route("/customer") {
    get {
        call.respondText("No customers found", status = HttpStatusCode.OK)
    }
    get("{id?}") {
        call.respondText("No customers found ID ${call.parameters.get("id")}", status = HttpStatusCode.OK)
    }
    post {
        call.respondText("No customers found POST", status = HttpStatusCode.OK)
    }
    delete("{id?}") {

    }

}