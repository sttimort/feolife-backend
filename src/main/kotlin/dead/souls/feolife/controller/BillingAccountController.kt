package dead.souls.feolife.controller

import dead.souls.feolife.model.FeolifePrincipal
import dead.souls.feolife.model.response.ExtensibleBillingAccount
import dead.souls.feolife.service.BillingAccountService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class BillingAccountController(
    private val billingAccountService: BillingAccountService,
) {
    @GetMapping("user-profiles/{userProfileUuid}/billing-account")
    fun get(
        @AuthenticationPrincipal principal: FeolifePrincipal,
        @PathVariable("userProfileUuid") userProfileUuid: String,
    ): ExtensibleBillingAccount =
        billingAccountService.getByUserProfileUuid(principal, userProfileUuid = UUID.fromString(userProfileUuid))
}
