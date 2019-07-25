package io.neolution.eventify.Services

import android.content.Context
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.Places
import android.support.v4.app.FragmentActivity
import android.content.Intent
import android.net.Uri
import io.neolution.eventify.Data.ModelClasses.indicate
import io.neolution.eventify.Utils.AppUtils.Companion.GOOGLE_MAPS_BASE_URL


/**
 * Created by Big-Nosed Developer on the Edge of Infinity.
 */
object GoogleServicesClass {

    fun startLocationActivity(raw_query: String, context: Context) {
        val searchQuery = raw_query.replace(" ", "+")

        val intentPlace = Uri.parse(GOOGLE_MAPS_BASE_URL + searchQuery)
        val mapIntent = Intent(Intent.ACTION_VIEW, intentPlace)
        mapIntent.setPackage("com.google.android.apps.maps")

        if (mapIntent.resolveActivity(context.packageManager) != null){
            context.startActivity(mapIntent)
        }else{
            context.indicate("Your Google Maps app is outdated!")
        }
    }
}