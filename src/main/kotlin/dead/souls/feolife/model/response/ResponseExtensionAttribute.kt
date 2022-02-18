package dead.souls.feolife.model.response

import dead.souls.feolife.model.response.ResponseExtensionAttributeName.AGE
import dead.souls.feolife.model.response.ResponseExtensionAttributeName.BALANCE
import dead.souls.feolife.model.response.ResponseExtensionAttributeName.BIRTH_DATE
import java.math.BigDecimal
import java.time.LocalDate

data class ResponseExtensionAttribute<T>(
    val name: String,
    val value: T,
) {
    companion object {
        fun birthDate(value: LocalDate?) = ResponseExtensionAttribute(BIRTH_DATE, value)
        fun age(value: Long?) = ResponseExtensionAttribute(AGE, value)
        fun balance(value: BigDecimal) = ResponseExtensionAttribute(BALANCE, value)
    }
}


private object ResponseExtensionAttributeName {
    const val BIRTH_DATE = "birthDate"
    const val AGE = "age"
    const val BALANCE = "balance"
}
