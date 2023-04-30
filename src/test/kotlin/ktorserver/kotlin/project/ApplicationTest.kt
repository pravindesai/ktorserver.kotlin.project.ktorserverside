package ktorserver.kotlin.project

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.testing.*
import kotlin.test.*
import io.ktor.http.*
import ktorserver.kotlin.project.plugins.*

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World!", bodyAsText())
        }
    }
}
