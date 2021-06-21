package com.diegoparra.veggie.user.address.usecases

import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.core.kotlin.*
import com.diegoparra.veggie.user.address.domain.Address
import com.diegoparra.veggie.user.address.domain.AddressRepository
import javax.inject.Inject

class GetAddressListAndSelectedIdUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val addressRepository: AddressRepository
) {

    /**
     *  Will return the address list and a selectedId if the list is not empty.
     *
     *  Important notes: How the selected id is got?
     *  ->  The selected id if it actually exists in the list.
     *  ->  The first id in the list, in case no id was previously selected or it corresponds to a
     *      deleted address.
     *      >> It will also save the first address as selected in the repository.
     *  ->  null if the addressList is empty.
     */
    suspend operator fun invoke(): Either<Failure, Pair<List<Address>, String?>> {
        return when (val currentUserId = authRepository.getIdCurrentUser()) {
            is Either.Left -> currentUserId
            is Either.Right -> {
                val addressList = addressRepository.getAddressList(currentUserId.b)
                val selectedAddressId = addressRepository.getSelectedAddressId(currentUserId.b)
                Either.combineMapSuspend(addressList, selectedAddressId) { list, selectedId ->
                    val selectedIdOrFirst = getSelectedIdOrSelectFirst(currentUserId = currentUserId.b, addressList = list, selectedId = selectedId)
                    Pair(first = list, second = selectedIdOrFirst)
                }
            }
        }
    }

    private suspend fun getSelectedIdOrSelectFirst(currentUserId: String, addressList: List<Address>, selectedId: String?): String? {
        return if(addressList.isEmpty()) {
            null
        } else {
            val address = addressList.find { it.id == selectedId }
            if(address == null) {
                val newSelectedAddress = addressList.first()
                addressRepository.setSelectedAddressId(
                    userId = currentUserId,
                    addressId = newSelectedAddress.id
                )
                addressList.first().id
            }else{
                address.id
            }
        }
    }

}