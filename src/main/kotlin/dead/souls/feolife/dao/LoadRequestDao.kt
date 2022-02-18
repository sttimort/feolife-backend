package dead.souls.feolife.dao

import dead.souls.feolife.model.LoanRequest
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Service
class LoadRequestDao(
    private val template: NamedParameterJdbcTemplate,
) {
    fun createLoadRequest(
        uuid: UUID,
        requesterId: Long,
        type: LoanRequest.Type,
        purpose: String,
        requestedAmount: BigDecimal,
        returnInMonths: Int,
        declaredJobPosition: String?,
        declaredMonthlyIncome: BigDecimal?,
        surety: String?,
        agreedToLifeInsurance: Boolean?,
        guarantor: String?,
        declaredGuarantorStatus: String?,
        details: String?,
        creationDatetime: Instant,
    ) {
        template.queryForObject(
            """
            INSERT INTO load_request(
                uuid, requester_id, type, purpose, requested_amount, return_in_months, declared_job_position, 
                declared_monthly_income, surety, agreed_to_life_insurance, guarantor_id, declared_guarantor_status, 
                details, creation_datetime, modification_datetime
            ) VALUES (
                :${Params.UUID}, :${Params.REQUESTER_ID}, :${Params.TYPE}, :${Params.PURPOSE},
                :${Params.REQUESTED_AMOUNT}, :${Params.RETURN_IN_MONTHS}, :${Params.DECLARED_JOB_POSITION},
                :${Params.DECLARED_MONTLY_INCOME}, :${Params.SURETY}, :${Params.AGREED_TO_LIFE_INSURANCE}, 
                :${Params.GUARANTOR_ID}, :${Params.DECLARED_GUARANTOR_STATUS}, :${Params.DETAILS}, 
                :${Params.CREATION_DATETIME}, :${Params.MODIFICATION_DATETIME}
            ) RETURNING id;
            """.trimIndent(),
            mapOf(
                Params.UUID to uuid,
                Params.REQUESTER_ID to requesterId,
                Params.TYPE to type,
                Params.PURPOSE to purpose,
                Params.REQUESTED_AMOUNT to requestedAmount,
                Params.RETURN_IN_MONTHS to returnInMonths,
                Params.DECLARED_JOB_POSITION to declaredJobPosition,
                Params.DECLARED_MONTLY_INCOME to declaredMonthlyIncome,
                Params.SURETY to surety,
                Params.AGREED_TO_LIFE_INSURANCE to agreedToLifeInsurance,
                Params.GUARANTOR_ID to guarantor,
                Params.DECLARED_GUARANTOR_STATUS to declaredGuarantorStatus,
                Params.DETAILS to details,
                Params.CREATION_DATETIME to creationDatetime,
                Params.MODIFICATION_DATETIME to creationDatetime,
            ),
            Long::class.java,
        )
    }

    companion object {
        private object Params {
            const val UUID = "uuid"
            const val REQUESTER_ID = "requester_id"
            const val TYPE = "type"
            const val PURPOSE = "purpose"
            const val REQUESTED_AMOUNT = "requested_amount"
            const val RETURN_IN_MONTHS = "return_in_months"
            const val DECLARED_JOB_POSITION = "declared_job_position"
            const val DECLARED_MONTLY_INCOME = "declared_monthly_income"
            const val SURETY = "surety"
            const val AGREED_TO_LIFE_INSURANCE = "agreed_to_life_insurance"
            const val GUARANTOR_ID = "guarantor_id"
            const val DECLARED_GUARANTOR_STATUS = "declared_guarantor_status"
            const val DETAILS = "details"
            const val CREATION_DATETIME = "creation_datetime"
            const val MODIFICATION_DATETIME = "modification_datetime"
        }
    }
}
