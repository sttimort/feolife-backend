package dead.souls.feolife.model.response

import dead.souls.feolife.model.Permission
import java.util.UUID

data class GetMyUserProfileResponse(
    val uuid: UUID,
    val firstName: String,
    val lastName: String,
    val middleName: String?,
    val credentials: List<Credentials>,
    val permissions: List<Permission>,
) {
    sealed class Credentials
    data class UsernameCredentials(val username: String): Credentials()
}
