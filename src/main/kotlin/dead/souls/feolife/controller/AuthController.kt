package dead.souls.feolife.controller

import dead.souls.feolife.model.FeolifePrincipal
import dead.souls.feolife.model.response.TokenAuthResponse
import dead.souls.feolife.service.JwtFactory
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(
    private val jwtFactory: JwtFactory,
) {
    @PostMapping("auth")
    fun jwtAuth(@AuthenticationPrincipal principal: FeolifePrincipal): TokenAuthResponse =
        TokenAuthResponse(jwtFactory.createForPrincipal(principal))

    @PostMapping("jwt-protected")
    fun decodeJwt(@AuthenticationPrincipal userProfileId: FeolifePrincipal) = userProfileId
}
