package dead.souls.feolife.dao.model

data class UsernamePasswordCredentials(
    val username: String,
    val password: String,
    val userProfileId: Long,
)
