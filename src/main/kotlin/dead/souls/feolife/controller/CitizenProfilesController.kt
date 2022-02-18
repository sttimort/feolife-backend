package dead.souls.feolife.controller

import dead.souls.feolife.model.FeolifePrincipal
import dead.souls.feolife.model.response.CitizensSearchResponse
import dead.souls.feolife.model.response.ExtensibleUserProfile
import dead.souls.feolife.service.CitizenProfileService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("citizens")
class CitizenProfilesController(
    private val citizenProfileService: CitizenProfileService,
) {
    @GetMapping
    fun search(
        @AuthenticationPrincipal principal: FeolifePrincipal,
        @RequestParam("query") query: String
    ): CitizensSearchResponse {
        return CitizensSearchResponse(citizenProfileService.search(principal, query))
    }
}
