package com.diegoparra.veggie.notifications

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        //  TODO:   Save token in database: to send notifications to specific client app and manage
        //          subscriptions on the server side.
        Timber.d("Refreshed token: $token")
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Timber.d("From ${remoteMessage.from}")
        Timber.d("message = $remoteMessage")
        super.onMessageReceived(remoteMessage)
    }
}