package dead.souls.feolife.controller

import dead.souls.feolife.model.response.TokenAuthResponse
import dead.souls.feolife.service.JwtFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class AuthController(
    private val jwtFactory: JwtFactory,
) {
    @PostMapping("auth")
    fun jwtAuth(@AuthenticationPrincipal userProfileId: UUID): TokenAuthResponse =
        TokenAuthResponse(jwtFactory.createWithUserProfileId(userProfileId))

    @PostMapping("jwt-protected")
    fun decodeJwt(@AuthenticationPrincipal userProfileId: UUID) = ResponseEntity.ok(userProfileId)
}
