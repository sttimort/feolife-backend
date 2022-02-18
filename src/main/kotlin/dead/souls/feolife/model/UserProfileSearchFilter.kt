package dead.souls.feolife.model

data class UserProfileSearchFilter(
    val textSearchTokens: List<String>,
    val types: List<UserProfile.Type> = emptyList(),
)
