package dead.souls.feolife.service

import dead.souls.feolife.dao.UserProfileDao
import dead.souls.feolife.model.Permission
import dead.souls.feolife.model.UserProfile.Type.CITIZEN
import dead.souls.feolife.model.UserProfileSearchFilter
import dead.souls.feolife.model.FeolifePrincipal
import dead.souls.feolife.model.response.ExtensibleUserProfile
import dead.souls.feolife.model.response.ResponseExtensionAttribute
import org.springframework.stereotype.Service

@Service
class CitizenProfileService(
    private val userProfileDao: UserProfileDao,
) {
    fun search(principal: FeolifePrincipal, queryString: String): List<ExtensibleUserProfile> {
        val searchTokens = queryString.split("\\s".toRegex()).filter { it.isNotBlank() }

        return userProfileDao
            .search(UserProfileSearchFilter(types = listOf(CITIZEN), textSearchTokens = searchTokens))
            .map {
                val attributes = mutableListOf<ResponseExtensionAttribute<out Any?>>()
                if (principal.permissions.contains(Permission.VIEW_BIRTHDATE_AND_AGE)) {
                    attributes.add(ResponseExtensionAttribute.birthDate(it.birthDate))
                    attributes.add(ResponseExtensionAttribute.age(it.age))
                }
                ExtensibleUserProfile(
                    uuid = it.uuid,
                    firstName = it.firstName,
                    lastName = it.lastName,
                    middleName = it.middleName,
                    gender = it.gender,
                    attributes = attributes,
                )
            }
    }
}
