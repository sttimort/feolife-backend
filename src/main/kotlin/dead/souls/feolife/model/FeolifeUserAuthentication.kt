package dead.souls.feolife.model

import org.springframework.security.core.Authentication

data class FeolifeUserAuthentication(
    val base: Authentication,
    private val principal: FeolifePrincipal,
) : Authentication by base {
    override fun getPrincipal(): FeolifePrincipal = principal
    override fun getAuthorities() = principal.permissions.map { it.grantedAuthority }
}
