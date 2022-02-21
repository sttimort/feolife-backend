package dead.souls.feolife.dao

import dead.souls.feolife.model.Permission
import dead.souls.feolife.model.UserProfile
import dead.souls.feolife.model.UserProfileSearchFilter
import dead.souls.feolife.service.containsAnyOf
import dead.souls.feolife.service.and
import dead.souls.feolife.service.given
import dead.souls.feolife.service.or
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service
import java.sql.ResultSet
import java.time.LocalDate
import java.util.UUID

private const val DEFAULT_QUERY_LIMIT = 5_000

@Service
class UserProfileDao(
    private val template: NamedParameterJdbcTemplate,
) {
    fun findUserProfileUuidWithPermissionsByUsername(username: String): Pair<UUID, List<Permission>>? = template
        .query(
            """
            SELECT user_profile.uuid AS ${Params.UUID}, role_permission.permission AS ${Params.PERMISSION}
            FROM username_password_credentials upc 
                JOIN user_profile ON upc.user_profile_id = user_profile.id
                LEFT JOIN user_role ON user_profile.id = user_role.user_profile_id
                LEFT JOIN role_permission on user_role.role_id = role_permission.role_id
            WHERE upc.username = :${Params.USERNAME}
            """.trimIndent(),
            mapOf(Params.USERNAME to username),
            UserProfileUuidWithPermissionsResultSetExtractor
        )

    fun findUserProfilePermissions(userProfileUuid: UUID): List<Permission> = template
        .query(
            """
            SELECT role_permission.permission AS ${Params.PERMISSION} FROM user_profile
                JOIN user_role ON user_profile.id = user_role.user_profile_id
                JOIN role_permission ON user_role.role_id = role_permission.role_id
            WHERE user_profile.uuid = :${Params.UUID}
            """.trimIndent(),
            mapOf(Params.UUID to userProfileUuid)
        ) { rs, _ -> enumValueOf<Permission>(rs.getString(Params.PERMISSION)) }
        .toList()

    fun findUserProfileByUuid(uuid: UUID): UserProfile? = template
        .query(
            "SELECT $ALL_USER_PROFILE_FIELDS FROM user_profile WHERE uuid = :${Params.UUID}",
            mapOf(Params.UUID to uuid),
            UserProfileRowMapper,
        )
        .singleOrNull()

    fun search(filter: UserProfileSearchFilter): List<UserProfile> {
        val whereCondition = given({ filter.types.isNotEmpty() }) { "type IN (:${Params.TYPE_LIST})" }
            .and(given = { filter.textSearchTokens.isNotEmpty() }) {
                ("first_name" containsAnyOf filter.textSearchTokens)
                    .or("last_name" containsAnyOf filter.textSearchTokens)
                    .or("middle_name" containsAnyOf filter.textSearchTokens)
            }

        return template.query(
            """
            SELECT $ALL_USER_PROFILE_FIELDS FROM user_profile
            WHERE $whereCondition
            LIMIT $DEFAULT_QUERY_LIMIT
            """.trimIndent(),
            mapOf<String, Any>(Params.TYPE_LIST to filter.types.map { it.name }),
            UserProfileRowMapper,
        )
            .toList()
    }

    fun createUserProfile(
        uuid: UUID,
        type: UserProfile.Type,
        billingAccountId: Long?,
        firstName: String,
        lastName: String,
        middleName: String?,
        gender: String?,
        birthDate: LocalDate?,
    ): Long = template
        .queryForObject(
            """
            INSERT INTO user_profile(
                uuid, type, billing_account_id, first_name, last_name, middle_name, gender, birth_date
            ) VALUES (
                :${Params.UUID}, :${Params.TYPE}, :${Params.BILLING_ACCOUNT_ID}, :${Params.FIRST_NAME}, 
                :${Params.LAST_NAME}, :${Params.MIDDLE_NAME}, :${Params.GENDER}, :${Params.BIRTH_DATE}
            )
            RETURNING id
            """.trimIndent(),
            mapOf(
                Params.UUID to uuid,
                Params.TYPE to type.name,
                Params.BILLING_ACCOUNT_ID to billingAccountId,
                Params.FIRST_NAME to firstName,
                Params.LAST_NAME to lastName,
                Params.MIDDLE_NAME to middleName,
                Params.GENDER to gender,
                Params.BIRTH_DATE to birthDate,
            ),
            Long::class.java,
        )
        .let { requireNotNull(it) }

    fun linkBillingAccount(userProfileId: Long, billingAccountId: Long) {
        template.update(
            "UPDATE user_profile SET billing_account_id = :${Params.BILLING_ACCOUNT_ID} WHERE id = :${Params.ID}",
            mapOf(
                Params.ID to userProfileId,
                Params.BILLING_ACCOUNT_ID to billingAccountId,
            ),
        )
    }

    fun assignRoles(userProfileId: Long, rolesIds: List<Long>) {
        if (rolesIds.isEmpty()) {
            return
        }

        template.batchUpdate(
            "INSERT INTO user_role(user_profile_id, role_id) VALUES (:${Params.USER_PROFILE_ID}, :${Params.ROLE_ID})",
            rolesIds
                .map {
                    mapOf(
                        Params.USER_PROFILE_ID to userProfileId,
                        Params.ROLE_ID to it,
                    )
                }
                .toTypedArray(),
        )
    }

    companion object {
        private const val ALL_USER_PROFILE_FIELDS =
            //@formatter:off
            "id as ${Params.ID}, type as ${Params.TYPE}, billing_account_id as ${Params.BILLING_ACCOUNT_ID}, " +
            "uuid as ${Params.UUID}, first_name as ${Params.FIRST_NAME}, last_name as ${Params.LAST_NAME}, " +
            "middle_name as ${Params.MIDDLE_NAME}, gender as ${Params.GENDER}, birth_date as ${Params.BIRTH_DATE}"
            //@formatter:on

        private object Params {
            const val ID = "id"
            const val UUID = "uuid"
            const val TYPE = "type"
            const val TYPE_LIST = "type_list"
            const val BILLING_ACCOUNT_ID = "billing_account_id"
            const val USERNAME = "username"
            const val FIRST_NAME = "first_name"
            const val LAST_NAME = "last_name"
            const val MIDDLE_NAME = "middle_name"
            const val GENDER = "gender"
            const val BIRTH_DATE = "birth_date"
            const val PERMISSION = "permission"
            const val USER_PROFILE_ID = "user_profile_id"
            const val ROLE_ID = "role_id"
        }

        private object UserProfileUuidWithPermissionsResultSetExtractor :
            ResultSetExtractor<Pair<UUID, List<Permission>>?> {
            override fun extractData(rs: ResultSet): Pair<UUID, List<Permission>>? {
                var uuid: UUID? = null
                val permissions = mutableListOf<Permission>()
                while (rs.next()) {
                    if (uuid == null) {
                        uuid = UUID.fromString(rs.getString(Params.UUID))
                    }
                    rs.getString(Params.PERMISSION)?.also { permissions.add(enumValueOf(it)) }
                }
                return uuid?.let { Pair(it, permissions) }
            }
        }

        private object UserProfileRowMapper : RowMapper<UserProfile> {
            override fun mapRow(rs: ResultSet, rowNum: Int): UserProfile = rs.run {
                UserProfile(
                    id = getLong(Params.ID),
                    uuid = UUID.fromString(getString(Params.UUID)),
                    type = enumValueOf(getString(Params.TYPE)),
                    billingAccountId = getLong(Params.BILLING_ACCOUNT_ID).takeIf { !wasNull() },
                    firstName = getString(Params.FIRST_NAME),
                    lastName = getString(Params.LAST_NAME),
                    middleName = getString(Params.MIDDLE_NAME),
                    gender = getString(Params.GENDER),
                    birthDate = getDate(Params.BIRTH_DATE)?.toLocalDate(),
                )
            }
        }
    }
}
