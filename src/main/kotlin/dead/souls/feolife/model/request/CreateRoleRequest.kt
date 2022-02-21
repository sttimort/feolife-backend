package dead.souls.feolife.model.request

import dead.souls.feolife.model.Permission

data class CreateRoleRequest(
    val name: String,
    val isAssignedOnUserProfileCreation: Boolean,
    val permissions: List<Permission>,
)
