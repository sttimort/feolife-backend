package dead.souls.feolife.model

import java.time.Instant
import java.util.UUID

data class RelocationRequest(
    val id: Long,
    val uuid: UUID,
    val requesterId: Long,
    val relocationInstant: Instant,
    val destinationAddress: Address,
    val status: Status,
    val creationInstant: Instant,
    val modificationInstant: Instant,
) {
    enum class Status { NEW, APPROVED, DECLINED, IN_PROGRESS, COMPLETED }
}
