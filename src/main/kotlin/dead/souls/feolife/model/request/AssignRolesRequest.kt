package dead.souls.feolife.model.request

import java.util.UUID

data class AssignRolesRequest(
    val roleUuids: List<UUID>,
)
