package dead.souls.feolife.validation.request

import dead.souls.feolife.model.request.BillingAccountFillUpRequest
import dead.souls.feolife.validation.positive
import dead.souls.feolife.validation.validateOrThrow
import io.konform.validation.Validation

private val billingAccountFillUpRequestValidation = Validation<BillingAccountFillUpRequest> {
//    BillingAccountFillUpRequest::value { positive() }
}

fun BillingAccountFillUpRequest.validate() = billingAccountFillUpRequestValidation.validateOrThrow(this)
