package dead.souls.feolife.model.request

import java.time.Instant

data class CreateRelocationRequestRequest(
    val relocationDate: Instant,
    val relocationDestination: Location,
) {
    data class Location(
        val city: String,
        val street: String,
        val house: String,
    )
}
