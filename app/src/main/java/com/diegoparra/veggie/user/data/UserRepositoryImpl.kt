package com.diegoparra.veggie.user.data

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.map
import com.diegoparra.veggie.products.IoDispatcher
import com.diegoparra.veggie.user.data.DtosTransformations.toAddress
import com.diegoparra.veggie.user.address.domain.Address
import com.diegoparra.veggie.user.data.DtosTransformations.toAddressDto
import com.diegoparra.veggie.user.data.firebase.UserApi
import com.diegoparra.veggie.user.data.prefs.UserPrefs
import com.diegoparra.veggie.user.User
import com.diegoparra.veggie.user.UserRepository
import com.diegoparra.veggie.user.data.DtosTransformations.toUser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    private val userPrefs: UserPrefs,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : UserRepository {

    override suspend fun updateUserData(
        userId: String,
        email: String?, name: String?,
        phoneNumber: String?, addressList: List<Address>?
    ): Either<Failure, Unit> = withContext(dispatcher) {
        Timber.d("updateUserData() called with: id = $userId, email = $email, name = $name, phoneNumber = $phoneNumber, address = $addressList")
        userApi.updateUserData(
            id = userId,
            email = email,
            name = name,
            phoneNumber = phoneNumber,
            addressList = addressList?.map { it.toAddressDto() }
        )
    }

    override suspend fun getUser(userId: String): Either<Failure, User> = withContext(dispatcher) {
        Timber.d("getUser() called with: id = $userId")
        userApi.getUser(userId).map { it.toUser() }
    }

    override suspend fun getAddressList(userId: String): Either<Failure, List<Address>> =
        withContext(dispatcher) {
            userApi.getUser(userId).map {
                it.addressList?.map { it.toAddress() } ?: emptyList()
            }
        }

    override suspend fun addAddress(userId: String, address: Address): Either<Failure, Unit> =
        withContext(dispatcher) {
            userApi.addAddress(
                userId = userId,
                addressDto = address.toAddressDto()
            )
        }

    override suspend fun deleteAddress(userId: String, address: Address): Either<Failure, Unit> =
        withContext(dispatcher) {
            userApi.deleteAddress(
                userId = userId,
                addressDto = address.toAddressDto()
            )
        }

    override suspend fun setSelectedAddressId(addressId: String) = withContext(dispatcher) {
        userPrefs.setSelectedAddress(addressId)
    }

    override suspend fun getSelectedAddressId(): Either<Failure, String> = withContext(dispatcher) {
        userPrefs.getSelectedAddress()?.let {
            Either.Right(it)
        } ?: Either.Left(Failure.ServerError(message = "No address has been saved as selected."))
    }

}