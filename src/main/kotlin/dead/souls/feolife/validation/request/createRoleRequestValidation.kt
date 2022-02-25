package dead.souls.feolife.validation.request

import dead.souls.feolife.model.request.CreateRoleRequest
import dead.souls.feolife.validation.notBlank
import dead.souls.feolife.validation.validateOrThrow
import io.konform.validation.Validation

private val createRoleRequestValidation = Validation<CreateRoleRequest> {
    CreateRoleRequest::name {
        notBlank()
    }
}

fun CreateRoleRequest.validate() = createRoleRequestValidation.validateOrThrow(this)
