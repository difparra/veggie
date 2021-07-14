package com.diegoparra.veggie.user.data.firebase

import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.core.kotlin.putIfNotNull
import com.diegoparra.veggie.user.data.firebase.UserFirebaseConstants.Keys
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class UserApi @Inject constructor(
    private val database: FirebaseFirestore
) {

    suspend fun updateUserData(
        id: String,
        email: String? = null,
        name: String? = null,
        phoneNumber: String? = null,
        addressList: List<AddressDto>? = null
    ): Either<Failure, Unit> {
        return try {
            //  Build the map with the data to be updated in user
            val data = mutableMapOf<String, Any>()
            data.putIfNotNull(Keys.email, email)
            data.putIfNotNull(Keys.name, name)
            data.putIfNotNull(Keys.phoneNumber, phoneNumber)
            data.putIfNotNull(Keys.addressList, addressList)

            //  Send instruction to Firebase to update the user
            database.collection(UserFirebaseConstants.Collections.user)
                .document(id)
                .set(data, SetOptions.merge())
                .await()

            //  Return Right if exceptions have not be thrown
            Either.Right(Unit)
        } catch (e: Exception) {
            Timber.e("Exception class=${e.javaClass}, message=${e.message}")
            Either.Left(Failure.ServerError(e))
        }
    }

    suspend fun getUser(id: String): Either<Failure, UserDto> {
        return try {
            val user = database
                .collection(UserFirebaseConstants.Collections.user)
                .document(id)
                .get()
                .await()
                .toObject<UserDto>()
            if(user != null) {
                Either.Right(user)
            } else {
                Timber.wtf("User with id = $id was not found.")
                Either.Left(Failure.NotFound)
            }
        } catch (e: Exception) {
            Timber.e("Exception class=${e.javaClass}, message=${e.message}")
            Either.Left(Failure.ServerError(e))
        }
    }

    suspend fun addAddress(userId: String, addressDto: AddressDto): Either<Failure, AddressDto> {
        return try {
            //  Send instruction to Firebase to update the user
            database.collection(UserFirebaseConstants.Collections.user)
                .document(userId)
                .update(Keys.addressList, FieldValue.arrayUnion(addressDto))
                .await()
            //  Return Right if exceptions have not be thrown
            Either.Right(addressDto)
        } catch (e: Exception) {
            Timber.e("Exception class=${e.javaClass}, message=${e.message}")
            Either.Left(Failure.ServerError(e))
        }
    }

    suspend fun deleteAddress(userId: String, addressDto: AddressDto): Either<Failure, Unit> {
        return try {
            //  Send instruction to Firebase to update the user
            database.collection(UserFirebaseConstants.Collections.user)
                .document(userId)
                .update(Keys.addressList, FieldValue.arrayRemove(addressDto))
                .await()
            //  Return Right if exceptions have not be thrown
            Either.Right(Unit)
        } catch (e: Exception) {
            Timber.e("Exception class=${e.javaClass}, message=${e.message}")
            Either.Left(Failure.ServerError(e))
        }
    }


}