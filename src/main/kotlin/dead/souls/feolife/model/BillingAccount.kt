package dead.souls.feolife.model

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class BillingAccount(
    val id: Long,
    val uuid: UUID,
    val balance: BigDecimal,
    val creationInstant: Instant,
    val modificationInstant: Instant,
)
