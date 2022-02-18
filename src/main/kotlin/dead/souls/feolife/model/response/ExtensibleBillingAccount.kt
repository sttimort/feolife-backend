package dead.souls.feolife.model.response

import java.util.UUID

data class ExtensibleBillingAccount(
    val uuid: UUID,
    val attributes: List<ResponseExtensionAttribute<out Any?>>,
)
