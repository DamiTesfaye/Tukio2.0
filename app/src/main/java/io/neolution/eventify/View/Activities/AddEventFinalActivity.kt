package io.neolution.eventify.View.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.LinearLayout
import android.widget.TextView
import io.neolution.eventify.Data.ModelClasses.EventsModel
import io.neolution.eventify.R
import io.neolution.eventify.Repos.AuthRepo
import io.neolution.eventify.Repos.FireStoreRepo
import io.neolution.eventify.Utils.AppUtils
import kotlinx.android.synthetic.main.activity_add_event_final.*
import kotlinx.android.synthetic.main.add_guest_bottomsheet.*

class AddEventFinalActivity : AppCompatActivity() {

    private lateinit var eventTitle: String
    private lateinit var eventDesc: String
    private lateinit var eventDate: String
    private lateinit var eventLocation: String
    private lateinit var eventTag: String
    private lateinit var eventPicUri: Uri
    private var eventMillis: Long? = null

    private lateinit var specialGuest: HashMap<String, Any>
    private lateinit var eventType: String

    private lateinit var addGuestBottomSheet: LinearLayout
    private lateinit var addGuestLayout: LinearLayout
    private lateinit var fireStoreRepo: FireStoreRepo
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event_final)

        addGuestBottomSheet = findViewById(R.id.add_guest_bsheet)
        addGuestLayout = findViewById(R.id.add_event_final_guest_container)
        specialGuest = HashMap()
        fireStoreRepo = FireStoreRepo()

        bottomSheetBehavior = BottomSheetBehavior.from(addGuestBottomSheet)

        add_guest_bsheet_close.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        add_event_final_add_guest_container.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        add_event_final_back_btn.setOnClickListener {
            val eventDressCode = add_event_final_dresscode_edit.text.toString().trim()
            val eventRegLink = add_event_final_reglink_edit.text.toString().trim()

            if(eventDressCode.isNotEmpty() || eventRegLink.isNotEmpty() || specialGuest.isNotEmpty()){
                val dialog = AlertDialog.Builder(this, R.style.MyTimePickerDialogTheme)
                dialog.setMessage("Do you want to discard your changes?")
                dialog.setPositiveButton("DISCARD") { _, _ ->
                    finish()
                }

                dialog.setNegativeButton("CANCEL") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }

                val alert = dialog.create()
                alert.show()
            }else{
                finish()
            }
        }

        add_guest_bsheet_add_guest_btn.setOnClickListener {
            val guestName = add_guest_bsheet_guest_name_edit.text.toString().trim()
            val guestDesc = add_guest_bsheet_guest_name_edit.text.toString().trim()

            if (guestName.isNotEmpty() && guestDesc.isNotEmpty()){
                specialGuest[guestName] = guestDesc

                val view = layoutInflater.inflate(R.layout.speaker_layout, addGuestLayout, false)
                val guestNameTv = view.findViewById<TextView>(R.id.speaker_layout_name_tv)
                val guestBioTv = view.findViewById<TextView>(R.id.speaker_layout_bio_tv)

                guestNameTv.text = guestName
                guestBioTv.text = guestDesc

                addGuestLayout.addView(view)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

            }else{
                if (guestName.isEmpty()){
                    add_guest_bsheet_guest_name_layout.isErrorEnabled = true
                    add_guest_bsheet_guest_name_layout.error = "Add the guest's name"
                }else if (guestDesc.isEmpty()){
                    add_guest_bsheet_guest_desc_layout.isErrorEnabled = true
                    add_guest_bsheet_guest_desc_layout.error = "Add the guest's description"
                }
            }
        }

        val bundle = intent.extras

        eventTitle = bundle.getString("eventTitle")
        eventDesc = bundle.getString("eventDesc")
        eventDate = bundle.getString("eventDate")
        eventLocation = bundle.getString("eventLocation")
        eventTag = bundle.getString("eventTag")
        eventPicUri = Uri.parse(bundle.getString("eventPicUri"))
        eventMillis = bundle.getLong("eventMillis")

        eventType = "normal" //"promoted"

        add_event_final_share_event_text.setOnClickListener {

            add_event_final_share_event_text.visibility = GONE
            add_event_final_share_event_container.background = ContextCompat.getDrawable(this, R.drawable.buttonbg_outline)
            add_event_final_share_event_progress.visibility = VISIBLE

            val eventDressCode = add_event_final_dresscode_edit.text.toString().trim()
            val eventRegLink = add_event_final_reglink_edit.text.toString().trim()
            val eventPostTime = System.currentTimeMillis()

            val eventModel = EventsModel(eventType = eventType, eventTitle = eventTitle,
                eventDesc = eventDesc, eventDate = eventDate, eventImageLink = eventPicUri.toString(), eventImageLinkThumb = "",
                eventLocation = eventLocation, eventDressCode = eventDressCode, eventRegLink = eventRegLink, eventTags = listOf(eventTag),
                eventMilis = eventMillis, eventGuests = specialGuest, eventPostTime = eventPostTime.toString(), eventTicketLink = "",
                userUID = AuthRepo.getUserUid(),amountPaid = 0)



            fireStoreRepo.postEvent(this, eventModel, {
                startActivity(Intent(this, HomeActivity::class.java))
            }, {

                add_event_final_share_event_text.visibility = VISIBLE
                add_event_final_share_event_container.background = ContextCompat.getDrawable(this, R.drawable.buttonbg)
                add_event_final_share_event_progress.visibility = GONE

                val v = findViewById<View>(android.R.id.content)
                AppUtils.getCustomSnackBar(v, it, this).show()

            })
        }

    }

    override fun onBackPressed() {
        val eventDressCode = add_event_final_dresscode_edit.text.toString().trim()
        val eventRegLink = add_event_final_reglink_edit.text.toString().trim()

        if(eventDressCode.isNotEmpty() || eventRegLink.isNotEmpty() || specialGuest.isNotEmpty()){
            val dialog = AlertDialog.Builder(this, R.style.MyTimePickerDialogTheme)
            dialog.setMessage("Do you want to discard your changes?")
            dialog.setPositiveButton("YES") { _, _ ->
                finish()
            }

            dialog.setNegativeButton("NO") { dialogInterface, _ ->

                dialogInterface.dismiss()
            }

            val alert = dialog.create()
            alert.show()
        }else{
            finish()
        }
    }
}
