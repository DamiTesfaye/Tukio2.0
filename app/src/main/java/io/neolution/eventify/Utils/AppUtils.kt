package io.neolution.eventify.Utils

import android.app.ProgressDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.google.android.material.snackbar.Snackbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import android.view.View
import android.widget.TextView
import android.widget.Toast
import io.neolution.eventify.Data.ModelClasses.ChipModel
import io.neolution.eventify.R

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
        const val APP_BASE_URL = "http://tukio.exceptos/events/"


        fun getCustomSnackBar(v: View, m: String, context: Context): Snackbar {
            val snackBar = Snackbar.make(v, m, Snackbar.LENGTH_LONG)
            val view = snackBar.view
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent))
            val textView = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            textView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))


            return snackBar
        }

        fun copyTextToClipBoard(text: String, context: Context){
            val clipBoard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Tukio-Event-Link", text)
            clipBoard.primaryClip = clip

            Toast.makeText(context, "Event link copied to clipboard", Toast.LENGTH_LONG)
                .show()
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
                    val manager = StaggeredGridLayoutManager(
                        1,
                        StaggeredGridLayoutManager.HORIZONTAL
                    )
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

        fun createEventLink(eventId: String): String{
            return "$APP_BASE_URL$eventId"
        }

        fun createChipList(): MutableList<ChipModel>{
            return mutableListOf(
                ChipModel("Art", R.drawable.ic_creativity),
                ChipModel("Trade Shows", R.drawable.ic_trade_show),
                ChipModel("Carnivals", R.drawable.ic_mask),
                ChipModel("Video Games", R.drawable.ic_game_controller),
                ChipModel("Law", R.drawable.ic_auction),
                ChipModel("Chess",R.drawable.ic_chess),
                ChipModel("Cosplay", R.drawable.ic_cosplayer),
                ChipModel("Exercise", R.drawable.ic_exercise),
                ChipModel("Weddings", R.drawable.ic_rings),
                ChipModel("Food", R.drawable.ic_apple),
                ChipModel("Travel", R.drawable.ic_world),
                ChipModel("Skating", R.drawable.ic_skateboard),
                ChipModel("Tennis", R.drawable.ic_tennis_ball),
                ChipModel("Tour", R.drawable.ic_road),
                ChipModel("Easter", R.drawable.ic_magic_hat),
                ChipModel("Eid-Al-Fitr", R.drawable.ic_islam),
                ChipModel("Eid-Al-Adha", R.drawable.ic_islam),
                ChipModel("New Year", R.drawable.ic_confetti2),
                ChipModel("Protest", R.drawable.ic_megaphone),
                ChipModel("Table Tennis", R.drawable.ic_ping_pong),
                ChipModel("Christmas", R.drawable.ic_santa_hat),
                ChipModel("Theatre", R.drawable.ic_puppet),
                ChipModel("Philosophy", R.drawable.ic_yin_yang),
                ChipModel("Festival", R.drawable.ic_band),
                ChipModel("Writing", R.drawable.ic_edit_vd),
                ChipModel("Contests", R.drawable.ic_competition),
                ChipModel("Tech", R.drawable.ic_electronics),
                ChipModel("Movement", R.drawable.ic_fist),
                ChipModel("MeetUp", R.drawable.ic_reunion),
                ChipModel("Business", R.drawable.ic_handshake),
                ChipModel("Concerts", R.drawable.ic_stage),
                ChipModel("Conferences", R.drawable.ic_lecture),
                ChipModel("Parties", R.drawable.ic_confetti),
                ChipModel("Workshops/Seminars", R.drawable.ic_analytics),
                ChipModel("Birthdays", R.drawable.ic_birthday_cake),
                ChipModel("Christian", R.drawable.ic_church),
                ChipModel("Muslim", R.drawable.ic_mosque),
                ChipModel("Football", R.drawable.ic_soccer),
                ChipModel("Basketball", R.drawable.ic_basketball),
                ChipModel("Tests", R.drawable.ic_test),
                ChipModel("Examinations", R.drawable.ic_exam),
                ChipModel("Movies", R.drawable.ic_popcorn)
            )
        }


    }

}



