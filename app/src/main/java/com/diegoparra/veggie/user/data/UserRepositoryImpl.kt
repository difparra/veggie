package com.diegoparra.veggie.user.data

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.map
import com.diegoparra.veggie.user.data.firebase.DtosTransformations.toAddress
import com.diegoparra.veggie.user.data.firebase.DtosTransformations.toUser
import com.diegoparra.veggie.address.Address
import com.diegoparra.veggie.user.data.firebase.DtosTransformations.toAddressDto
import com.diegoparra.veggie.user.data.firebase.UserApi
import com.diegoparra.veggie.user.data.prefs.UserPrefs
import com.diegoparra.veggie.user.domain.User
import com.diegoparra.veggie.user.domain.UserRepository
import timber.log.Timber
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    private val userPrefs: UserPrefs
) : UserRepository {

    override suspend fun updateUserData(
        userId: String,
        email: String?, name: String?,
        phoneNumber: String?, addressList: List<Address>?
    ): Either<Failure, Unit> {
        Timber.d("updateUserData() called with: id = $userId, email = $email, name = $name, phoneNumber = $phoneNumber, address = $addressList")
        return userApi.updateUserData(
            id = userId,
            email = email,
            name = name,
            phoneNumber = phoneNumber,
            addressList = addressList?.map { it.toAddressDto() }
        )
    }

    override suspend fun getUser(userId: String): Either<Failure, User> {
        Timber.d("getUser() called with: id = $userId")
        return userApi.getUser(userId).map { it.toUser() }
    }

    override suspend fun getAddressList(userId: String): Either<Failure, List<Address>> {
        return userApi.getUser(userId).map {
            it.addressList?.map { it.toAddress() } ?: emptyList()
        }
    }

    override suspend fun addAddress(userId: String, address: Address): Either<Failure, Unit> {
        return userApi.addAddress(
            userId = userId,
            addressDto = address.toAddressDto()
        )
    }

    override suspend fun deleteAddress(userId: String, address: Address): Either<Failure, Unit> {
        return userApi.deleteAddress(
            userId = userId,
            addressDto = address.toAddressDto()
        )
    }

    override suspend fun setSelectedAddressId(addressId: String) {
        return userPrefs.setSelectedAddress(addressId)
    }

    override suspend fun getSelectedAddressId(): Either<Failure, String> {
        return userPrefs.getSelectedAddress()?.let {
            Either.Right(it)
        } ?: Either.Left(Failure.ServerError(message = "No address has been saved as selected."))
    }

}