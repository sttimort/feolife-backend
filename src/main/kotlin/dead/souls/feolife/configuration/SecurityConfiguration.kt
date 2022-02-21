package dead.souls.feolife.configuration

import dead.souls.feolife.filter.AuthenticatedUserEnrichmentFilter
import dead.souls.feolife.model.Permission
import dead.souls.feolife.model.Permission.BILLING_ACCOUNT_FILL_UP
import dead.souls.feolife.model.Permission.CREATE_ROLE
import dead.souls.feolife.model.Permission.LIST_ROLES
import dead.souls.feolife.model.Permission.LOGIN
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.web.servlet.AuthorizeRequestsDsl
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter
import org.springframework.security.web.authentication.AuthenticationFilter
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.www.BasicAuthenticationConverter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfiguration(
    private val decoder: JwtDecoder,
    private val authenticatedUserEnrichmentFilter: AuthenticatedUserEnrichmentFilter,
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) = http {
        csrf { disable() }

        cors {
            configurationSource = UrlBasedCorsConfigurationSource().apply {
                registerCorsConfiguration("/**", CorsConfiguration().apply { applyPermitDefaultValues() })
            }
        }

        http.addFilterAt(
            AuthenticationFilter(authenticationManager(), BasicAuthenticationConverter()).apply {
                requestMatcher = AntPathRequestMatcher("/auth")
                successHandler = AuthenticationSuccessHandler { _, _, _ -> }
            },
            UsernamePasswordAuthenticationFilter::class.java,
        )

        http.addFilterAfter(authenticatedUserEnrichmentFilter, AnonymousAuthenticationFilter::class.java)

        oauth2ResourceServer {
            jwt {
                jwtDecoder = decoder
            }
        }

        authorizeRequests {
            authorize("/username-password-profiles", permitAll)

            authorize("/auth", hasPermission(LOGIN))
            authorize("/my-user-profile", authenticated)

            authorize(HttpMethod.GET, "/roles", hasPermission(LIST_ROLES))
            authorize(HttpMethod.POST, "/roles", hasPermission(CREATE_ROLE))
            authorize("/billing-accounts/*/fill-ups", hasPermission(BILLING_ACCOUNT_FILL_UP))
        }
    }
}

private fun AuthorizeRequestsDsl.hasPermission(permission: Permission) = hasAuthority(permission.name)

