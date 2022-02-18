package dead.souls.feolife.service

import dead.souls.feolife.dao.BillingAccountDao
import dead.souls.feolife.dao.DuplicateUsernameException
import dead.souls.feolife.dao.RoleDao
import dead.souls.feolife.dao.UserProfileDao
import dead.souls.feolife.dao.UsernamePasswordCredentialsDao
import dead.souls.feolife.exception.FeolifeStatusConflictException
import dead.souls.feolife.exception.FeolifeStatusNotFoundException
import dead.souls.feolife.logger
import dead.souls.feolife.model.UserProfile
import dead.souls.feolife.model.request.CreateUserProfileWithUsernamePasswordCredentialsRequest
import dead.souls.feolife.model.response.GetUserProfileCommonDataSetResponse
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
class UserProfileManager(
    private val userProfileDao: UserProfileDao,
    private val usernamePasswordCredentialsDao: UsernamePasswordCredentialsDao,
    private val billingAccountDao: BillingAccountDao,
    private val roleDao: RoleDao,
) {
    private val passwordEncoder: PasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()

    fun getUserProfileCommonDataSet(userProfileUuid: UUID): GetUserProfileCommonDataSetResponse = userProfileDao
        .findUserProfileByUuid(userProfileUuid)
        ?.let { userProfile ->
            GetUserProfileCommonDataSetResponse(
                uuid = userProfile.uuid,
                firstName = userProfile.firstName,
                lastName = userProfile.lastName,
                middleName = userProfile.middleName,
                credentials = usernamePasswordCredentialsDao.findAllWithUserProfileId(userProfile.id).map {
                    GetUserProfileCommonDataSetResponse.UsernameCredentials(it.username)
                }
            )
        }
        ?: throw FeolifeStatusNotFoundException(responseErrorMessage = "UserProfile with uuid $userProfileUuid not found")

    @Transactional
    fun createUserProfile(request: CreateUserProfileWithUsernamePasswordCredentialsRequest) {
        checkUsernameNotRegistered(username = request.username)

        val userProfileId = userProfileDao.createUserProfile(
            uuid = UUID.randomUUID(),
            type = UserProfile.Type.CITIZEN,
            billingAccountId = null,
            firstName = request.firstName,
            lastName = request.lastName,
            middleName = request.middleName,
            gender = null,
            birthDate = null,
        )
        log.info { "Created user profile with id $userProfileId" }

        try {
            val credentialsId = usernamePasswordCredentialsDao.createCredentials(
                username = request.username,
                password = passwordEncoder.encode(request.password),
                userProfileId = userProfileId,
            )
            log.info { "Created username password credentials with id $credentialsId for userProfileId $userProfileId" }
        } catch (exception: DuplicateUsernameException) {
            log.error(exception) { "Duplication username while creating credentials for userProfileId $userProfileId" }
            throw UsernameAlreadyRegisteredException(request.username)
        }

        try {
            val billingAccountId = billingAccountDao.createBillingAccount(UUID.randomUUID(), Instant.now())
            userProfileDao.linkBillingAccount(userProfileId = userProfileId, billingAccountId = billingAccountId)
            log.info { "Created and linked billing account $billingAccountId for user profile $userProfileId" }
        } catch (exception: Exception) {
            log.error(exception) { "Failed to create and link billing account for user profile $userProfileId" }
            throw exception;
        }

        val rolesToAssign = roleDao.getRolesAssignedOnUserProfileCreation()
        try {
            userProfileDao.assignRoles(userProfileId, rolesToAssign.map { it.id })
            log.info { "Assigned roles $rolesToAssign to user profile $userProfileId" }
        } catch (exception: Exception) {
            log.error(exception) { "Failed to assign roles $rolesToAssign to user profile $userProfileId" }
        }
    }

    private fun checkUsernameNotRegistered(username: String) {
        if (usernamePasswordCredentialsDao.findByUsername(username) != null) {
            throw UsernameAlreadyRegisteredException(username)
        }
    }

    companion object {
        private val log by logger()
    }
}

class UsernameAlreadyRegisteredException(username: String) : FeolifeStatusConflictException(
    responseErrorMessage = "Username $username already registered",
    message = "Username $username already registered",
)
