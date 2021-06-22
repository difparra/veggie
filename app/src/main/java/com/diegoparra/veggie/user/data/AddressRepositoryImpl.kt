package com.diegoparra.veggie.user.data

import com.diegoparra.veggie.core.android.IoDispatcher
import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.core.kotlin.map
import com.diegoparra.veggie.user.address.domain.Address
import com.diegoparra.veggie.user.address.domain.AddressRepository
import com.diegoparra.veggie.user.data.DtosTransformations.toAddress
import com.diegoparra.veggie.user.data.DtosTransformations.toAddressDto
import com.diegoparra.veggie.user.data.firebase.UserApi
import com.diegoparra.veggie.user.data.prefs.UserPrefs
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddressRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    private val userPrefs: UserPrefs,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : AddressRepository {

    override suspend fun getAddressList(userId: String): Either<Failure, List<Address>> =
        withContext(dispatcher) {
            userApi.getUser(userId).map {
                it.addressList?.map { it.toAddress() } ?: emptyList()
            }
        }

    override suspend fun addAddress(userId: String, address: Address): Either<Failure, Address> =
        withContext(dispatcher) {
            userApi.addAddress(
                userId = userId,
                addressDto = address.toAddressDto()
            ).map { it.toAddress() }
        }

    override suspend fun deleteAddress(userId: String, address: Address): Either<Failure, Unit> =
        withContext(dispatcher) {
            //  It is necessary to also delete from selected, to keep consistency in selectedAddress.
            userPrefs.deleteAddressAsSelectedIfApplicable(userId, address.id)
            userApi.deleteAddress(
                userId = userId,
                addressDto = address.toAddressDto()
            )
        }


    override suspend fun setSelectedAddress(userId: String, addressId: String) =
        withContext(dispatcher) {
            userPrefs.setSelectedAddress(userId = userId, addressId = addressId)
        }


    /**
     * Get the selected address in a safe way: AddressId is verified to actually exists in the
     * addressList associated to the user.
     * Why safe way?
     *   This is important, as selected address will be saved locally, and therefore, it will not
     *   listen to external changes. For example, some user has selected an address in phone #1, and
     *   later in phone #2 they delete it. Phone #2 will listen the change, and delete the address from
     *   prefs, but phone #1 won't and therefore it will have in prefs an nonexistent address.
     * Additional:
     * - This method was placed here and not in useCase as it actually depends on repository.
     *   If I decided to implement the repository only remote, in other words, if selectedAddressId were
     *   fetched remotely, it wouldn't need the additional verification on addressList.
     * - Normally, when getting the selectedAddress, addressList has been previously fetched. So,
     *   in order to save a database operation I could pass it as parameter. However, if not
     *   addressList is passed, the repo will calculate by itself.
     */
    override suspend fun getSelectedAddress(
        userId: String,
        addressList: List<Address>?
    ): Either<Failure, Address?> {
        //  As addressList will be normally fetched before getting the selected address, to simplify
        //  an operation I could get it as argument. However, if not, I could also get using the repo.
        val mAddressList = addressList ?: getAddressList(userId).let {
            when (it) {
                is Either.Left -> return@getSelectedAddress Either.Left(it.a)
                is Either.Right -> return@let it.b
            }
        }

        //  Get address or null if addressId is not found in addressList or savedAddressId is null
        return if (mAddressList.isEmpty()) {
            Either.Right(null)
        } else {
            val selectedAddressId = userPrefs.getSelectedAddress(userId = userId)
            if (selectedAddressId == null) {
                Either.Right(null)
            } else {
                Either.Right(mAddressList.find { it.id == selectedAddressId })
            }
        }
    }

}