package dead.souls.feolife.dao

import dead.souls.feolife.model.Address
import dead.souls.feolife.model.RelocationRequest
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class RelocationRequestDao(
    private val template: NamedParameterJdbcTemplate,
) {
    fun createRelocationRequest(
        uuid: UUID,
        requesterId: Long,
        relocationInstant: Instant,
        destinationAddress: Address,
        status: RelocationRequest.Status,
        creationInstant: Instant,
        details: String?,
    ) {
        template.queryForObject(
            """
            INSERT INTO relocation_request(
                uuid, requester_id, relocation_datetime, destination_city, destination_street,destination_building, 
                status, creation_datetime, modification_datetime, details
            ) VALUES (
                :${Params.UUID}, :${Params.REQUESTER_ID}, :${Params.RELOCATION_DATETIME}, :${Params.DESTINATION_CITY}, 
                :${Params.DESTINATION_STREET}, ${Params.DESTINATION_BUILDING}, :${Params.STATUS}, 
                :${Params.CREATION_DATETIME}, :${Params.MODIFICATION_DATETIME}, :${Params.DETAILS}
            ) RETURNING id;
            """.trimIndent(),
            mapOf(
                Params.UUID to uuid,
                Params.REQUESTER_ID to requesterId,
                Params.RELOCATION_DATETIME to relocationInstant,
                Params.DESTINATION_CITY to destinationAddress.city,
                Params.DESTINATION_STREET to destinationAddress.street,
                Params.DESTINATION_BUILDING to destinationAddress.building,
                Params.STATUS to status,
                Params.CREATION_DATETIME to creationInstant,
                Params.MODIFICATION_DATETIME to creationInstant,
                Params.DETAILS to details,
            ),
            Long::class.java,
        )
    }

    private object Params {
        const val UUID = "uuid"
        const val REQUESTER_ID = "requester_id"
        const val RELOCATION_DATETIME = "relocation_datetime"
        const val DESTINATION_CITY = "destination_city"
        const val DESTINATION_STREET = "destination_street"
        const val DESTINATION_BUILDING = "destination_building"
        const val STATUS = "status"
        const val CREATION_DATETIME = "creation_datetime"
        const val MODIFICATION_DATETIME = "modification_datetime"
        const val DETAILS = "details"
    }
}
