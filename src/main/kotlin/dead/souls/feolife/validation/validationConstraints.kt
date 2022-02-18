package dead.souls.feolife.validation

import io.konform.validation.Constraint
import io.konform.validation.ValidationBuilder
import java.math.BigDecimal

fun ValidationBuilder<String>.notBlank(): Constraint<String> =
    addConstraint("must not be blank") { it.isNotBlank() }

fun ValidationBuilder<BigDecimal>.positive(): Constraint<BigDecimal> =
    addConstraint("must be greater than zero") { it > BigDecimal.ZERO }
