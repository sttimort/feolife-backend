package dead.souls.feolife.service

import dead.souls.feolife.dao.UsernamePasswordCredentialsDao
import dead.souls.feolife.model.Permission
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class FeolifeUserDetailsService(
    private val usernamePasswordCredentialsDao: UsernamePasswordCredentialsDao,
) : UserDetailsService {
    override fun loadUserByUsername(username: String?): UserDetails =
        username
            ?.let { usernamePasswordCredentialsDao.findByUsername(username) }
            ?.let { User(it.username, it.password, listOf(Permission.LOGIN.grantedAuthority)) }
            ?: throw UsernameNotFoundException("Username $username not found")
}
