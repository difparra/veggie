package com.diegoparra.veggie.user.data

import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.core.android.IoDispatcher
import com.diegoparra.veggie.core.kotlin.map
import com.diegoparra.veggie.user.address.domain.Address
import com.diegoparra.veggie.user.data.DtosTransformations.toAddressDto
import com.diegoparra.veggie.user.data.firebase.UserApi
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

}