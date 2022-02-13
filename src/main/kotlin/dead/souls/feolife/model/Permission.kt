package dead.souls.feolife.model

import org.springframework.security.core.authority.SimpleGrantedAuthority

enum class Permission {
    LOGIN;

    val grantedAuthority = SimpleGrantedAuthority(name)
}
