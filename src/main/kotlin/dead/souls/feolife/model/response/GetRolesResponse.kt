package dead.souls.feolife.model.response

import java.util.UUID

data class GetRolesResponse(
    val roles: List<Role>
) {
    data class Role(
        val uuid: UUID,
        val name: String,
        val isAssignedOnUserProfileCreation: Boolean,
    )
}
