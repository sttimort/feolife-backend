package dead.souls.feolife.model.response

data class GetRolesResponse(
    val roles: List<Role>
) {
    data class Role(
        val name: String,
        val isAssignedOnUserProfileCreation: Boolean,
    )
}
