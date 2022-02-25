package dead.souls.feolife.model

import org.springframework.security.core.authority.SimpleGrantedAuthority

enum class Permission {
    LOGIN,

    // profile
    VIEW_BIRTHDATE_AND_AGE,

    // roles
    LIST_ROLES,
    CREATE_ROLE,
    DELETE_ROLE,
    ASSIGN_ROLES,

    // role permissions
    LIST_ROLE_PERMISSIONS,
    CHANGE_ROLE_PERMISSIONS,

    QUERY_BILLING_ACCOUNT,
    VIEW_BILLING_ACCOUNT_BALANCE,

    BILLING_ACCOUNT_FILL_UP,

    ;

    val grantedAuthority = SimpleGrantedAuthority(name)
}
