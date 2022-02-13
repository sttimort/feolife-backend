package dead.souls.feolife.model

import org.springframework.security.core.Authentication
import java.util.UUID

data class FeolifeUserAuthentication(
    val base: Authentication,
    private val userProfileUuid: UUID,
) : Authentication by base {
    override fun getPrincipal(): UUID = userProfileUuid
}
