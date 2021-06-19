package com.diegoparra.veggie.user.address.usecases

import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.auth.utils.AuthFailure
import com.diegoparra.veggie.core.kotlin.*
import com.diegoparra.veggie.user.address.domain.Address
import com.diegoparra.veggie.user.address.domain.AddressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetAddressUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val addressRepository: AddressRepository
) {

    suspend fun getAddressList(): Either<Failure, List<Address>> {
        return authRepository.getIdCurrentUser().suspendFlatMap {
            addressRepository.getAddressList(it)
        }
    }

    suspend fun getSelectedAddressId(): String? {
        val currentUserId = authRepository.getIdCurrentUser().getOrElse("")
        return when (val addressId =
            addressRepository.getSelectedAddressId(userId = currentUserId)) {
            is Either.Left -> null
            is Either.Right -> addressId.b
        }
    }

    fun getSelectedAddressAsFlow(currentUserIdAsFlow: Flow<Either<AuthFailure, String>>? = null): Flow<Either<Failure, Address?>> {
        val userIdAsFlow = currentUserIdAsFlow ?: authRepository.getIdCurrentUserAsFlow()
        return userIdAsFlow.flatMapLatest {
            when (it) {
                is Either.Left -> flow { emit(it) }
                is Either.Right -> addressRepository.getSelectedAddressAsFlow(it.b)
            }
        }
    }

}