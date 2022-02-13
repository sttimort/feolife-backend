package dead.souls.feolife.configuration

import com.nimbusds.jose.jwk.RSAKey
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder

@Configuration
class JwtConfiguration {
    @Bean
    fun rsaJwk(): RSAKey =
        ClassPathResource("keys/private_rsa.json", JwtConfiguration::class.java.classLoader)
            .inputStream
            .reader()
            .readText()
            .let { RSAKey.parse(it) }

    @Bean
    fun jwtDecoder(): JwtDecoder {
        val rsaPublicKey = ClassPathResource("keys/public_rsa.json", JwtConfiguration::class.java.classLoader)
            .inputStream
            .reader()
            .readText()
            .let { RSAKey.parse(it) }
            .toRSAPublicKey()

        return NimbusJwtDecoder.withPublicKey(rsaPublicKey).signatureAlgorithm(SignatureAlgorithm.RS256).build()
    }
}
