package dead.souls.feolife.dao

import dead.souls.feolife.model.UserProfile
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service
import java.sql.ResultSet
import java.util.UUID

@Service
class UserProfileDao(
    private val template: NamedParameterJdbcTemplate,
) {
    fun findUserProfileUuidByUsername(username: String) = template
        .queryForList(
            """
            SELECT user_profile.uuid as ${Params.UUID}
            FROM username_password_credentials upc JOIN user_profile ON upc.user_profile_id = user_profile.id
            WHERE upc.username = :${Params.USERNAME}
            """.trimIndent(),
            mapOf(Params.USERNAME to username),
            UUID::class.java,
        )
        .singleOrNull()

    fun findUserProfileByUuid(uuid: UUID): UserProfile? = template
        .query(
            """
            SELECT
                id as ${Params.ID},
                uuid as ${Params.UUID},
                first_name as ${Params.FIRST_NAME},
                last_name as ${Params.LAST_NAME},
                middle_name as ${Params.MIDDLE_NAME}
            FROM user_profile
            WHERE uuid = :${Params.UUID}
            """.trimIndent(),
            mapOf(Params.UUID to uuid),
            UserProfileRowMapper,
        )
        .singleOrNull()

    fun createUserProfile(
        uuid: UUID,
        firstName: String,
        lastName: String,
        middleName: String?,
    ): Long = template
        .queryForObject(
            """
            INSERT INTO user_profile(uuid, first_name, last_name, middle_name)
            VALUES (:${Params.UUID}, :${Params.FIRST_NAME}, :${Params.LAST_NAME}, :${Params.MIDDLE_NAME})
            RETURNING id
            """.trimIndent(),
            mapOf(
                Params.UUID to uuid,
                Params.FIRST_NAME to firstName,
                Params.LAST_NAME to lastName,
                Params.MIDDLE_NAME to middleName,
            ),
            Long::class.java,
        )
        .let { requireNotNull(it) }

    private object Params {
        const val ID = "id"
        const val UUID = "uuid"
        const val USERNAME = "username"
        const val FIRST_NAME = "first_name"
        const val LAST_NAME = "last_name"
        const val MIDDLE_NAME = "middle_name"
    }

    private object UserProfileRowMapper : RowMapper<UserProfile> {
        override fun mapRow(rs: ResultSet, rowNum: Int): UserProfile = rs.run {
            UserProfile(
                id = getLong(Params.ID),
                uuid = UUID.fromString(getString(Params.UUID)),
                firstName = getString(Params.FIRST_NAME),
                lastName = getString(Params.LAST_NAME),
                middleName = getString(Params.MIDDLE_NAME),
            )
        }
    }
}
