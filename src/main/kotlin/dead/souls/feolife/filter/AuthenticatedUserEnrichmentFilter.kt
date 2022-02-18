package dead.souls.feolife.filter

import dead.souls.feolife.dao.UserProfileDao
import dead.souls.feolife.model.FeolifeUserAuthentication
import dead.souls.feolife.model.FeolifePrincipal
import dead.souls.feolife.service.FeolifeJwtClaimNames
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.UUID
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AuthenticatedUserEnrichmentFilter(
    private val userProfileDao: UserProfileDao,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        SecurityContextHolder.getContext()?.authentication?.let { authentication ->
            val enrichedAuthentication: FeolifeUserAuthentication? = when (authentication) {
                is UsernamePasswordAuthenticationToken -> enrichUsernamePasswordAuthentication(authentication)
                is JwtAuthenticationToken -> enrichJwtAuthentication(authentication)
                is AnonymousAuthenticationToken -> null
                else -> throw IllegalStateException("$authentication has unsupported type")
            }

            if (enrichedAuthentication != null) {
                val enrichedSecurityContext = SecurityContextHolder.createEmptyContext().also {
                    it.authentication = enrichedAuthentication
                }
                SecurityContextHolder.setContext(enrichedSecurityContext)
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun enrichUsernamePasswordAuthentication(authentication: UsernamePasswordAuthenticationToken): FeolifeUserAuthentication {
        val userDetails = authentication.principal
            ?.let { it as? UserDetails }
            ?: throw IllegalStateException(
                "Invalid UsernamePasswordAuthentication $authentication: principal is not UserDetails"
            )

        val principal = userDetails.username
            ?.let { userProfileDao.findUserProfileUuidWithPermissionsByUsername(it) }
            ?.let { (userProfileUuid, permissions) ->
                FeolifePrincipal(userProfileUuid = userProfileUuid, permissions = permissions)
            }
            ?: throw IllegalStateException(
                "UserProfile not found by username ${userDetails.username} from $authentication"
            )

        return FeolifeUserAuthentication(
            base = authentication,
            principal = principal
        )
    }

    private fun enrichJwtAuthentication(authentication: JwtAuthenticationToken): FeolifeUserAuthentication {
        val jwt = authentication.principal
            ?.let { it as? Jwt }
            ?: throw IllegalStateException("Invalid JwtAuthenticationToken $authentication: principal is not Jwt")

        return FeolifeUserAuthentication(
            base = authentication,
            principal = FeolifePrincipal(
                userProfileUuid = UUID.fromString(jwt.subject),
                permissions = jwt.getClaimAsStringList(FeolifeJwtClaimNames.PERMISSIONS)
                    .map { enumValueOf(it) }
            )
        )
    }
}
