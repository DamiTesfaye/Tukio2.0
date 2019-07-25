package io.neolution.eventify.Init_ers

import android.app.Application
import co.paystack.android.PaystackSdk
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

/**
 * Created by Big-Nosed Developer on the Edge of Infinity.
 */
class Initializer: Application() {

    override fun onCreate() {
        super.onCreate()

        val db = FirebaseFirestore.getInstance()

        //Initializing firebase settings
        val fireStoreSettings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .setPersistenceEnabled(true)
            .build()
        db.firestoreSettings = fireStoreSettings

        //Initializing PayStack settings
        PaystackSdk.initialize(applicationContext)
        PaystackSdk.setPublicKey("pk_live_9b8dfc20061b994b9ffc25f56befaff053e35b19")

    }
}