package dead.souls.feolife.model.response

import java.util.UUID

data class GetUserProfileRolesResponse(
    val roles: List<Role>,
) {
    data class Role(
        val uuid: UUID,
        val name: String,
    )
}
