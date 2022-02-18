package dead.souls.feolife.model

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.util.UUID

data class UserProfile(
    val id: Long,
    val uuid: UUID,
    val type: Type,
    val billingAccountId: Long?,
    val firstName: String,
    val lastName: String,
    val middleName: String?,
    val gender: String?,
    val birthDate: LocalDate?,
) {
    val age: Long?
        get() = birthDate?.until(LocalDate.from(Instant.now().atOffset(ZoneOffset.UTC)), ChronoUnit.YEARS)

    enum class Type { CITIZEN, PUBLIC_SERVICE }
}
