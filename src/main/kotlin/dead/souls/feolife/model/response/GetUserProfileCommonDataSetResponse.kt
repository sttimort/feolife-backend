package dead.souls.feolife.model.response

import java.util.UUID

data class GetUserProfileCommonDataSetResponse(
    val uuid: UUID,
    val firstName: String,
    val lastName: String,
    val middleName: String?,
    val credentials: List<Credentials>,
) {
    sealed class Credentials
    data class UsernameCredentials(val username: String): Credentials()
}
