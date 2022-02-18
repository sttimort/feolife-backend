package dead.souls.feolife.dao

import dead.souls.feolife.model.BillingAccount
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.sql.ResultSet
import java.sql.Timestamp
import java.time.Instant
import java.util.UUID

@Service
class BillingAccountDao(
    private val template: NamedParameterJdbcTemplate,
) {
    fun getByUserProfileUuid(userProfileUuid: UUID): BillingAccount? = template
        .query(
            """
            SELECT * FROM billing_account JOIN user_profile ON user_profile.billing_account_id = billing_account.id
            WHERE user_profile.uuid = :${Params.UUID}
            """.trimIndent(),
            mapOf(
                Params.UUID to userProfileUuid
            ),
            BillingAccountRowMapper,
        )
        .singleOrNull()

    fun createBillingAccount(
        uuid: UUID,
        creationInstant: Instant,
    ): Long = template
        .queryForObject(
            """
            INSERT INTO billing_account(uuid, balance, creation_datetime, modification_datetime)
            VALUES (:${Params.UUID}, :${Params.BALANCE}, :${Params.CREATION_DATETIME}, :${Params.MODIFICATION_DATETIME})
            RETURNING id;
            """.trimIndent(),
            mapOf(
                Params.UUID to uuid,
                Params.BALANCE to BigDecimal.ZERO,
                Params.CREATION_DATETIME to Timestamp.from(creationInstant),
                Params.MODIFICATION_DATETIME to Timestamp.from(creationInstant),
            ),
            Long::class.java,
        )
        .let { requireNotNull(it) }

    fun updateBalanceByUuid(billingAccountUuid: UUID, updateBy: BigDecimal) {
        val affectedRowsQuantity = template.update(
            "UPDATE billing_account SET balance = balance + :${Params.BALANCE} WHERE uuid = :${Params.UUID}",
            mapOf(
                Params.UUID to billingAccountUuid,
                Params.BALANCE to updateBy,
            )
        )

        if (affectedRowsQuantity < 1) {
            throw BillingAccountNotFoundByUuidException(uuid = billingAccountUuid)
        }
    }

    companion object {
        private object Params {
            const val ID = "id"
            const val UUID = "uuid"
            const val BALANCE = "balance"
            const val CREATION_DATETIME = "creation_datetime"
            const val MODIFICATION_DATETIME = "modification_datetime"
        }

        private object BillingAccountRowMapper : RowMapper<BillingAccount> {
            override fun mapRow(rs: ResultSet, rowNum: Int): BillingAccount = rs.run {
                BillingAccount(
                    id = getLong(Params.ID),
                    uuid = UUID.fromString(getString(Params.UUID)),
                    balance = getBigDecimal(Params.BALANCE),
                    creationInstant = getTimestamp(Params.CREATION_DATETIME).toInstant(),
                    modificationInstant = getTimestamp(Params.MODIFICATION_DATETIME).toInstant(),
                )
            }

        }
    }
}

class BillingAccountNotFoundByUuidException(uuid: UUID) : RuntimeException("Billing account $uuid not found")
