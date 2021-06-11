package com.diegoparra.veggie.user.data

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.putIfNotNull
import com.diegoparra.veggie.user.data.UserFirebaseConstants.Keys
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserApi @Inject constructor(
    private val database: FirebaseFirestore
) {

    suspend fun updateUserData(
        id: String,
        email: String? = null,
        name: String? = null,
        phoneNumber: String? = null,
        address: String? = null
    ): Either<Failure, Unit> {
        return try {
            //  Build the map with the data to be updated in user
            val data = mutableMapOf<String, String>()
            data.putIfNotNull(Keys.id, id)
            data.putIfNotNull(Keys.email, email)
            data.putIfNotNull(Keys.name, name)
            data.putIfNotNull(Keys.phoneNumber, phoneNumber)
            data.putIfNotNull(Keys.address, address)

            //  Send instruction to Firebase to update the user
            database.collection(UserFirebaseConstants.Collections.user)
                .document(id)
                .set(data, SetOptions.merge())
                .await()

            //  Return Right if exceptions have not be thrown
            Either.Right(Unit)
        } catch (e: Exception) {
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
            user?.let {
                Either.Right(it)
            } ?: Either.Left(Failure.UserFailure.UserNotFound)
        } catch (e: Exception) {
            Either.Left(Failure.ServerError(e))
        }
    }


}