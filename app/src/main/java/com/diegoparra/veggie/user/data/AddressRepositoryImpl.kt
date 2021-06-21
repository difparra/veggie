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


    override suspend fun setSelectedAddressId(userId: String, addressId: String) =
        withContext(dispatcher) {
            userPrefs.setSelectedAddress(userId = userId, addressId = addressId)
        }

    override suspend fun getSelectedAddressId(userId: String): Either<Failure, String?> =
        withContext(dispatcher) {
            Either.Right(userPrefs.getSelectedAddress(userId = userId))
        }


}