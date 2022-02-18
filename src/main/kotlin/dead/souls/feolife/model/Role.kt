package dead.souls.feolife.model

data class Role(
    val id: Long,
    val name: String,
    val isAssignedOnUserProfileCreation: Boolean,
)
