package dead.souls.feolife.model

import java.util.UUID

data class FeolifePrincipal(
    val userProfileUuid: UUID,
    val permissions: List<Permission>,
)
