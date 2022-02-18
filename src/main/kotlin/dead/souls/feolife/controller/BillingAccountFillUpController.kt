package dead.souls.feolife.controller

import dead.souls.feolife.model.request.BillingAccountFillUpRequest
import dead.souls.feolife.service.BillingAccountService
import dead.souls.feolife.validation.request.validate
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class BillingAccountFillUpController(
    private val billingAccountService: BillingAccountService,
) {
    @PostMapping("billing-accounts/{billingAccountUuid}/fill-ups")
    fun fillUp(
        @PathVariable("billingAccountUuid") billingAccountUuid: String,
        @RequestBody body: BillingAccountFillUpRequest,
    ) {
        body.validate()
        billingAccountService.fillUp(UUID.fromString(billingAccountUuid), amount = body.value)
    }
}
