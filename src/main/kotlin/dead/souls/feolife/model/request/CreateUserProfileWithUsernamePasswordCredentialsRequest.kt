package dead.souls.feolife.model.request

data class CreateUserProfileWithUsernamePasswordCredentialsRequest(
    val username: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val middleName: String?,
)
