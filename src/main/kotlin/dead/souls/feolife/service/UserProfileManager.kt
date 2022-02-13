package dead.souls.feolife.service

import dead.souls.feolife.dao.DuplicateUsernameException
import dead.souls.feolife.dao.UserProfileDao
import dead.souls.feolife.dao.UsernamePasswordCredentialsDao
import dead.souls.feolife.exception.FeolifeStatusConflictException
import dead.souls.feolife.exception.FeolifeStatusNotFoundException
import dead.souls.feolife.logger
import dead.souls.feolife.model.request.CreateUserProfileWithUsernamePasswordCredentialsRequest
import dead.souls.feolife.model.response.GetUserProfileCommonDataSetResponse
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UserProfileManager(
    private val userProfileDao: UserProfileDao,
    private val usernamePasswordCredentialsDao: UsernamePasswordCredentialsDao,
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
            firstName = request.firstName,
            lastName = request.lastName,
            middleName = request.middleName,
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
