package ktorserver.kotlin.project

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.cio.*
import io.ktor.server.locations.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.resources.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import ktorserver.kotlin.project.db.DatabaseFactory
import ktorserver.kotlin.project.plugins.BaseRouting
import ktorserver.kotlin.project.plugins.configureSerialization
import java.nio.charset.StandardCharsets.UTF_8
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

//http://127.0.0.1:8989/customer
fun main(args: Array<String>) {
    EngineMain.main(args)

}

@OptIn(KtorExperimentalLocationsAPI::class)
fun Application.module() {
    DatabaseFactory.init()

    install(CORS) { anyHost() }
    install(Sessions){
//        cookie<>(AppKeys.KEY_COOKIES)
    }
    install(Resources)
    install(Locations)
    configureSerialization()
    BaseRouting()
}
