package com.exceptos.tukio.Utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.ui.PlacePicker


/**
 * Created by Big-Nosed Developer on the Edge of Infinity.
 */
class IntentUtils {

    companion object {

        const val  photoIntentUniqueID: Int = 2556
        const val photoPermissionRequestCode = 123
        var PLACE_PICKER_REQUEST_CODE = 1005

        fun selectPhotoIntent(): Intent{
            val intent = Intent()
            return intent.apply{
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
            }
        }

        @Throws(GooglePlayServicesNotAvailableException::class, GooglePlayServicesRepairableException::class)
        fun startPlacePicker(activity: Activity): Intent {

            val builder = PlacePicker.IntentBuilder()
            return builder.build(activity)

        }

        fun shareEvent(context: Context, eventTitle: String, eventLocation: String, eventDate: String, eventID: String){
            val shareIntent = Intent()

            val eventLink = AppUtils.createEventLink(eventID)
            shareIntent.action = Intent.ACTION_SEND

            val finalText = "${eventTitle.capitalize()} will be taking place at $eventLocation. The time for attendance is $eventDate. \n \n \n" +
                    "This event is brought to you by Tukio! To view the further descriptions, click the following link: $eventLink"

            shareIntent.putExtra(Intent.EXTRA_TEXT, finalText)
            shareIntent.type = "text/plain"
            context.startActivity(Intent.createChooser(shareIntent, "Share Event!!"))
        }

    }

}