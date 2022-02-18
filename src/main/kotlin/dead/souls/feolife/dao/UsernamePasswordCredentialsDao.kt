package dead.souls.feolife.dao

import dead.souls.feolife.model.UsernamePasswordCredentials
import org.springframework.dao.DuplicateKeyException
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service
import java.sql.ResultSet

@Service
class UsernamePasswordCredentialsDao(
    private val template: NamedParameterJdbcTemplate,
) {
    fun findByUsername(username: String): UsernamePasswordCredentials? = template
        .query(
            """
            SELECT 
                username as ${Params.USERNAME},
                password as ${Params.PASSWORD},
                user_profile_id as ${Params.USER_PROFILE_ID}
            FROM username_password_credentials
            WHERE username = :${Params.USERNAME}
            """.trimIndent(),
            mapOf(Params.USERNAME to username),
            UsernamePasswordCredentialsRowMapper,
        )
        .singleOrNull()

    fun findAllWithUserProfileId(userProfileId: Long): List<UsernamePasswordCredentials> = template
        .query(
            """
            SELECT 
                username as ${Params.USERNAME},
                password as ${Params.PASSWORD},
                user_profile_id as ${Params.USER_PROFILE_ID}
            FROM username_password_credentials
            WHERE user_profile_id = :${Params.USER_PROFILE_ID}
            """.trimIndent(),
            mapOf(Params.USER_PROFILE_ID to userProfileId),
            UsernamePasswordCredentialsRowMapper,
        )
        .toList()

    fun createCredentials(username: String, password: String, userProfileId: Long): Long = try {
        template
            .queryForObject(
                """
                INSERT INTO username_password_credentials (username, password, user_profile_id)
                VALUES (:${Params.USERNAME}, :${Params.PASSWORD}, :${Params.USER_PROFILE_ID})
                RETURNING id
                """.trimIndent(),
                mapOf(
                    Params.USERNAME to username,
                    Params.PASSWORD to password,
                    Params.USER_PROFILE_ID to userProfileId,
                ),
                Long::class.java,
            )
            .let { requireNotNull(it) }
    } catch (exception: DuplicateKeyException) {
        throw DuplicateUsernameException(username, cause = exception)
    }


    private object Params {
        const val USERNAME = "username"
        const val PASSWORD = "password"
        const val USER_PROFILE_ID = "user_profile_id"
    }

    private object UsernamePasswordCredentialsRowMapper : RowMapper<UsernamePasswordCredentials> {
        override fun mapRow(rs: ResultSet, rowNum: Int): UsernamePasswordCredentials = rs.run {
            UsernamePasswordCredentials(
                username = getString(Params.USERNAME),
                password = getString(Params.PASSWORD),
                userProfileId = getLong(Params.USER_PROFILE_ID),
            )
        }
    }
}

class DuplicateUsernameException(username: String, cause: Throwable) :
    RuntimeException("Username $username already exists", cause)
