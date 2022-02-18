package dead.souls.feolife.dao

import dead.souls.feolife.model.Role
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service
import java.sql.ResultSet

@Service
class RoleDao(
    private val template: NamedParameterJdbcTemplate,
) {
    fun getRolesAssignedOnUserProfileCreation(): List<Role> = template
        .query(
            "SELECT * FROM role WHERE is_assigned_on_profile_creation = true",
            emptyMap<String, Any>(),
            RoleRowMapper,
        )
        .toList()

    companion object {
        private object Params {
            const val ID = "id"
            const val NAME = "name"
            const val IS_ASSIGNED_ON_USER_PROFILE_CREATION = "is_assigned_on_profile_creation"
        }

        private object RoleRowMapper : RowMapper<Role> {
            override fun mapRow(rs: ResultSet, rowNum: Int): Role = rs.run {
                Role(
                    id = getLong(Params.ID),
                    name = getString(Params.NAME),
                    isAssignedOnUserProfileCreation = getBoolean(Params.IS_ASSIGNED_ON_USER_PROFILE_CREATION),
                )
            }
        }
    }
}
