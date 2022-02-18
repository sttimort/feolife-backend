package dead.souls.feolife.model

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class LoanRequest(
    val id: Long,
    val uuid: UUID,
    val requesterId: Long,
    val type: Type,
    val purpose: String,
    val requestedAmount: BigDecimal,
    val returnInMonths: Int,
    val declaredJobPosition: String?,
    val declaredMonthlyIncome: BigDecimal?,
    val surety: String?,
    val agreedToLifeInsurance: Boolean?,
    val guarantorId: Long?,
    val declaredGuarantorStatus: String?,
    val details: String?,
    val creationDatetime: Instant,
    val modificationDatetime: Instant,
) {
    enum class Type {
        ACCOMMODATION, TRANSPORT, OTHER
    }
}
