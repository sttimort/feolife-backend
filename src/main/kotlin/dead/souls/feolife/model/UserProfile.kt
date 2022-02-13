package dead.souls.feolife.model

import java.util.UUID

data class UserProfile(
    val id: Long,
    val uuid: UUID,
    val firstName: String,
    val lastName: String,
    val middleName: String?,
)
