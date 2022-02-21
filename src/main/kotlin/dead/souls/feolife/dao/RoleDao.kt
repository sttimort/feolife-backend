package dead.souls.feolife.dao

import dead.souls.feolife.model.Permission
import dead.souls.feolife.model.Role
import org.springframework.dao.DuplicateKeyException
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service
import java.sql.ResultSet

private const val DEFAULT_LIMIT = 5_000

@Service
class RoleDao(
    private val template: NamedParameterJdbcTemplate,
) {
    fun getRoles(): List<Role> = template
        .query(
            "SELECT * FROM role LIMIT $DEFAULT_LIMIT",
            emptyMap<String, Any>(),
            RoleRowMapper,
        )
        .toList()

    fun getRolePermissions(roleName: String): List<Permission> = template
        .query(
            """
            SELECT permission FROM role JOIN role_permission ON role.id = role_permission.role_id
            WHERE role.name = :${Params.NAME}
            """.trimIndent(),
            mapOf(
                Params.NAME to roleName,
            ),
        ) { rs, _ -> enumValueOf<Permission>(rs.getString(Params.PERMISSION))  }
        .toList()

    fun createRole(
        name: String,
        isAssignedOnUserProfileCreation: Boolean,
    ): Role = try {
        template
            .queryForObject(
                """
            INSERT INTO role(name, is_assigned_on_profile_creation)
            VALUES (:${Params.NAME}, :${Params.IS_ASSIGNED_ON_USER_PROFILE_CREATION})
            RETURNING *
            """.trimIndent(),
                mapOf(
                    Params.NAME to name,
                    Params.IS_ASSIGNED_ON_USER_PROFILE_CREATION to isAssignedOnUserProfileCreation,
                ),
                RoleRowMapper,
            )
            .let { requireNotNull(it) }
    } catch (e: DuplicateKeyException) {
        throw DuplicateRoleNameException(name)
    }

    fun assignPermissions(roleId: Long, permissions: List<Permission>) {
        template.batchUpdate(
            "INSERT INTO role_permission(role_id, permission) VALUES (:${Params.ROLE_ID}, :${Params.PERMISSION})",
            permissions
                .map {
                    mapOf(
                        Params.ROLE_ID to roleId,
                        Params.PERMISSION to it.name,
                    )
                }
                .toTypedArray()
        )
    }

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
            const val ROLE_ID = "role_id"
            const val PERMISSION = "permission"
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

class DuplicateRoleNameException(name: String) : RuntimeException("Role with name $name already exists")
