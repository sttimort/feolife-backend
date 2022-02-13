package dead.souls.feolife.service

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import dead.souls.feolife.model.Permission
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.util.Date
import java.util.UUID

@Service
class JwtFactory(
    rsaJwk: RSAKey,
) {
    private val jwtSigner = RSASSASigner(rsaJwk)
    private val jwsHeader = JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaJwk.keyID).build()

    fun createWithUserProfileId(userProfileId: UUID): String =
        SignedJWT(
            jwsHeader,
            JWTClaimsSet.Builder()
                .subject(userProfileId.toString())
                .issuer("https://accounts.feolife.souls.dead")
                .claim("permissions", listOf(Permission.LOGIN))
                .issueTime(Date.from(Instant.now()))
                .expirationTime(Date.from(Instant.now().plus(Duration.ofHours(1))))
                .build(),
        )
            .apply { sign(jwtSigner) }
            .serialize()
}
