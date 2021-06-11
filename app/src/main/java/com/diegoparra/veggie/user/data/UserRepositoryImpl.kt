package com.diegoparra.veggie.user.data

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.map
import com.diegoparra.veggie.user.data.DtosTransformations.toAddress
import com.diegoparra.veggie.user.data.DtosTransformations.toUser
import com.diegoparra.veggie.user.domain.Address
import com.diegoparra.veggie.user.domain.User
import com.diegoparra.veggie.user.domain.UserRepository
import timber.log.Timber
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi
) : UserRepository {

    override suspend fun updateUserData(
        id: String,
        email: String?, name: String?,
        phoneNumber: String?, address: String?
    ): Either<Failure, Unit> {
        Timber.d("updateUserData() called with: id = $id, email = $email, name = $name, phoneNumber = $phoneNumber, address = $address")
        return userApi.updateUserData(
            id = id,
            email = email,
            name = name,
            phoneNumber = phoneNumber,
            address = address
        )
    }

    override suspend fun getUser(id: String): Either<Failure, User> {
        Timber.d("getUser() called with: id = $id")
        return userApi.getUser(id).map { it.toUser() }
    }

    override suspend fun getAddressListForUser(id: String): Either<Failure, List<Address>> {
        return userApi.getUser(id).map {
            it.address?.map { it.toAddress() } ?: emptyList()
        }
    }

}