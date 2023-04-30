package ktorserver.kotlin.project.models

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
data class Customer(val id:Int,val name:String?, val password:String?)