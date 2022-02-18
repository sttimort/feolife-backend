package dead.souls.feolife.service

import dead.souls.feolife.dao.BillingAccountDao
import dead.souls.feolife.dao.BillingAccountNotFoundByUuidException
import dead.souls.feolife.exception.FeolifeStatusNotFoundException
import dead.souls.feolife.model.FeolifePrincipal
import dead.souls.feolife.model.Permission
import dead.souls.feolife.model.response.ExtensibleBillingAccount
import dead.souls.feolife.model.response.ResponseExtensionAttribute
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.UUID

@Service
class BillingAccountService(
    private val billingAccountDao: BillingAccountDao,
) {
    fun getByUserProfileUuid(principal: FeolifePrincipal, userProfileUuid: UUID): ExtensibleBillingAccount {
        val billingAccount = billingAccountDao
            .getByUserProfileUuid(userProfileUuid)
            ?: throw FeolifeStatusNotFoundException("Billing account for user $userProfileUuid not found")

        val attributes = mutableListOf<ResponseExtensionAttribute<out Any?>>()
        if (principal.permissions.contains(Permission.VIEW_BILLING_ACCOUNT_BALANCE)) {
            attributes.add(ResponseExtensionAttribute.balance(billingAccount.balance))
        }

        return ExtensibleBillingAccount(
            uuid = billingAccount.uuid,
            attributes = attributes,
        )
    }

    fun fillUp(billingAccountUuid: UUID, amount: BigDecimal) {
        if (amount <= BigDecimal.ZERO) {
            throw IllegalArgumentException("Can't fill up billing account by negative amount")
        }
        try {
            billingAccountDao.updateBalanceByUuid(billingAccountUuid, updateBy = amount)
        } catch (e: BillingAccountNotFoundByUuidException) {
            throw FeolifeStatusNotFoundException("Billing account $billingAccountUuid not found")
        }
    }
}
