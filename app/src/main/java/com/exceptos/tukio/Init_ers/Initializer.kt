package com.exceptos.tukio.Init_ers

import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import co.paystack.android.PaystackSdk
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

/**
 * Created by Big-Nosed Developer on the Edge of Infinity.
 */
class Initializer: MultiDexApplication() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

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