package com.diegoparra.veggie.user.address.usecases

import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.core.kotlin.*
import com.diegoparra.veggie.user.address.domain.Address
import com.diegoparra.veggie.user.address.domain.AddressRepository
import javax.inject.Inject

class GetSelectedAddressUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val addressRepository: AddressRepository
) {

    /**
     * Get the selected address in a safe way (verifying that the selected address actually exists
     * in the addressList of the user).
     * This is important, as selected address will be saved locally, and therefore, it will not
     * listen to external changes. For example, some user has selected an address in phone #1, and
     * later in phone #2 they delete it. Phone #2 will listen the change, and delete the address from
     * prefs, but phone #1 won't and therefore it will have in prefs an nonexistent address.
     * TODO: Move to repository and delete method getSelectedAddressId
     * This useCase could actually be in the repository.
     */

    suspend operator fun invoke(): Either<Failure, Address?> {
        return when (val currentUserId = authRepository.getIdCurrentUser()) {
            is Either.Left -> currentUserId
            is Either.Right -> {
                val addressList = addressRepository.getAddressList(currentUserId.b)
                val selectedAddressId = addressRepository.getSelectedAddressId(currentUserId.b)
                Either.combineMapSuspend(addressList, selectedAddressId) { list, selectedId ->
                    list.find { it.id == selectedId }
                }
            }
        }
    }

    /* A slightly improved method compared to the one above, as it will accept addressList and userId
     * if they were already loaded, instead of calling to get the value.
     * AddressList will be usually loaded with selectedAddress, so this method allows not to reload that again.
     */
    suspend operator fun invoke(
        userId: String? = null,
        addressList: List<Address>? = null
    ): Either<Failure, Address?> {
        val mUserId = userId ?: authRepository.getIdCurrentUser().let {
            when (it) {
                is Either.Left -> return@invoke Either.Left(it.a)
                is Either.Right -> return@let it.b
            }
        }
        val mAddressList = addressList ?: addressRepository.getAddressList(mUserId).let {
            when (it) {
                is Either.Left -> return@invoke Either.Left(it.a)
                is Either.Right -> return@let it.b
            }
        }

        if (mAddressList.isEmpty()) {
            return Either.Right(null)
        }
        val selectedAddressId = addressRepository.getSelectedAddressId(mUserId)
        return selectedAddressId.map { selectedId ->
            if (selectedId == null) {
                null
            } else {
                mAddressList.find { it.id == selectedId }
            }
        }
    }

}