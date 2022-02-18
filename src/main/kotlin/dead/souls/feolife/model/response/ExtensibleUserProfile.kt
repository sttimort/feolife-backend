package dead.souls.feolife.model.response

import java.util.UUID

data class ExtensibleUserProfile(
    val uuid: UUID,
    val firstName: String,
    val lastName: String,
    val middleName: String?,
    val gender: String?,
    val attributes: List<ResponseExtensionAttribute<out Any?>>,
)
