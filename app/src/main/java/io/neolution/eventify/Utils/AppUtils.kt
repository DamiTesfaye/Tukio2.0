package io.neolution.eventify.Utils

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import android.widget.TextView
import com.google.android.gms.common.api.GoogleApiClient
import io.neolution.eventify.R
import io.neolution.eventify.Services.GoogleServicesClass
import java.util.*

/**
 * Created by Big-Nosed Developer on the Edge of Infinity.
 */
class AppUtils {

    companion object {

        val fillInAllFieldsError = "Please fill in all the fields"

        const val NOTIFICATION_CHANNEL_ID = "eventify_notification_channel_id"

        const val STAGGERED_LAYOUT_MANAGER = "staggered_layout_manager"
        const val LINEAR_LAYOUT_MANAGER = "linear_layout_manager"

        const val GOOGLE_MAPS_BASE_URL = "geo:0,0?q="
        const val APP_BASE_URL = "http://tuk.io.events/"


        fun getCustomSnackBar(v: View, m: String, context: Context): Snackbar{
            val snackBar = Snackbar.make(v, m, Snackbar.LENGTH_LONG)
            val view = snackBar.view
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent))
            val textView = view.findViewById<TextView>(android.support.design.R.id.snackbar_text)
            textView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))


            return snackBar
        }

        fun instantiateProgressDialog(message: String, context: Context): ProgressDialog{
           val dialog = ProgressDialog(context)
            return dialog.apply {
                setMessage(message)
                setCanceledOnTouchOutside(false)
                isIndeterminate = true
            }


        }


        fun getRecycleLayout(layoutManagerType: String, context: Context): RecyclerView.LayoutManager?{
            return when (layoutManagerType){

                AppUtils.STAGGERED_LAYOUT_MANAGER -> {
                    val manager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL)
                    manager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
                    manager
                }

                AppUtils.LINEAR_LAYOUT_MANAGER -> {
                    val manager = LinearLayoutManager(context)
                    manager
                }else -> {
                    throw Exception("layoutManagerType must be of either AppUtils.STAGGERED_LAYOUT_MANAGER or AppUtils.LINEAR_LAYOUT_MANAGER")
                }


            }
        }



    }

}

