package dead.souls.feolife.model

import java.util.UUID

data class Role(
    val id: Long,
    val uuid: UUID,
    val name: String,
    val isAssignedOnUserProfileCreation: Boolean,
)
