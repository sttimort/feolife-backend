package dead.souls.feolife.dao

import dead.souls.feolife.model.Permission
import dead.souls.feolife.model.Role
import org.springframework.dao.DuplicateKeyException
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.ResultSet
import java.util.UUID

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

    fun getRolePermissionsByRoleUuid(roleUuid: UUID): List<Permission> = template
        .query(
            """
            SELECT permission FROM role JOIN role_permission ON role.id = role_permission.role_id
            WHERE role.uuid = :${Params.UUID}
            """.trimIndent(),
            mapOf(
                Params.UUID to roleUuid,
            ),
        ) { rs, _ -> enumValueOf<Permission>(rs.getString(Params.PERMISSION)) }
        .toList()

    fun getRolesByUserProfileId(userProfileId: Long): List<Role> = template
        .query(
            """
            SELECT role.*
            FROM user_role JOIN role ON role.id = user_role.role_id
            WHERE user_role.user_profile_id = :${Params.USER_PROFILE_ID}
            """.trimIndent(),
            mapOf(Params.USER_PROFILE_ID to userProfileId),
            RoleRowMapper,
        )
        .toList()

    fun createRole(
        uuid: UUID,
        name: String,
        isAssignedOnUserProfileCreation: Boolean,
    ): Role = try {
        template
            .queryForObject(
                """
            INSERT INTO role(uuid, name, is_assigned_on_profile_creation)
            VALUES (:${Params.UUID}, :${Params.NAME}, :${Params.IS_ASSIGNED_ON_USER_PROFILE_CREATION})
            RETURNING *
            """.trimIndent(),
                mapOf(
                    Params.UUID to uuid,
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

    @Transactional
    fun setRolePermissionsByRoleUuid(roleUuid: UUID, permissions: List<Permission>) {
        val roleId = template
            .queryForObject(
                "SELECT id FROM role WHERE uuid = :${Params.UUID}",
                mapOf(Params.UUID to roleUuid),
                Long::class.java
            ) ?: throw RoleNotFoundException(roleUuid.toString())

        template.update("DELETE FROM role_permission WHERE role_id = :${Params.ID}", mapOf(Params.ID to roleId))
        assignPermissions(roleId, permissions)
    }

    fun deleteRoleByUuid(roleUuid: UUID) {
        template.update(
            "DELETE FROM role WHERE uuid = :${Params.UUID}",
            mapOf(Params.UUID to roleUuid),
        )
    }

    @Transactional
    fun assignRoles(userProfileId: Long, roleUuids: List<UUID>) {
        val roleIds =
            if (roleUuids.isNotEmpty()) {
                template.queryForList(
                    "SELECT id FROM role WHERE uuid in (:${Params.UUID})",
                    mapOf(Params.UUID to roleUuids),
                    Long::class.java,
                )
            } else emptyList()

        template.update(
            "DELETE FROM user_role WHERE user_profile_id = :${Params.USER_PROFILE_ID}",
            mapOf(Params.USER_PROFILE_ID to userProfileId),
        )
        if (roleIds.isNotEmpty()) {
            template.batchUpdate(
                """
                INSERT INTO user_role(user_profile_id, role_id) VALUES (:${Params.USER_PROFILE_ID}, :${Params.ROLE_ID})
                """.trimIndent(),
                roleIds
                    .map {
                        mapOf(
                            Params.USER_PROFILE_ID to userProfileId,
                            Params.ROLE_ID to it,
                        )
                    }
                    .toTypedArray(),
            )
        }
    }

    companion object {
        private object Params {
            const val ID = "id"
            const val UUID = "uuid"
            const val NAME = "name"
            const val IS_ASSIGNED_ON_USER_PROFILE_CREATION = "is_assigned_on_profile_creation"
            const val ROLE_ID = "role_id"
            const val PERMISSION = "permission"
            const val USER_PROFILE_ID = "user_profile_id"
        }

        private object RoleRowMapper : RowMapper<Role> {
            override fun mapRow(rs: ResultSet, rowNum: Int): Role = rs.run {
                Role(
                    id = getLong(Params.ID),
                    uuid = UUID.fromString(getString(Params.UUID)),
                    name = getString(Params.NAME),
                    isAssignedOnUserProfileCreation = getBoolean(Params.IS_ASSIGNED_ON_USER_PROFILE_CREATION),
                )
            }
        }
    }
}

class DuplicateRoleNameException(name: String) : RuntimeException("Role with name $name already exists")
class RoleNotFoundException(id: String) : RuntimeException("Role $id not found")
