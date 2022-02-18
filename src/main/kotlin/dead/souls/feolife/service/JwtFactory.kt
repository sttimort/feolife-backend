package dead.souls.feolife.service

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import dead.souls.feolife.model.FeolifePrincipal
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.util.Date

@Service
class JwtFactory(
    rsaJwk: RSAKey,
) {
    private val jwtSigner = RSASSASigner(rsaJwk)
    private val jwsHeader = JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaJwk.keyID).build()

    fun createForPrincipal(feolifePrincipal: FeolifePrincipal): String =
        SignedJWT(
            jwsHeader,
            JWTClaimsSet.Builder()
                .subject(feolifePrincipal.userProfileUuid.toString())
                .issuer("https://accounts.feolife.souls.dead")
                .claim(FeolifeJwtClaimNames.PERMISSIONS, feolifePrincipal.permissions)
                .issueTime(Date.from(Instant.now()))
                .expirationTime(Date.from(Instant.now().plus(Duration.ofHours(1))))
                .build(),
        )
            .apply { sign(jwtSigner) }
            .serialize()
}

object FeolifeJwtClaimNames {
    const val PERMISSIONS = "permissions"
}
