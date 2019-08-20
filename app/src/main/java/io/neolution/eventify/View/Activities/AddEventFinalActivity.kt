package io.neolution.eventify.View.Activities

import android.app.Activity
import android.content.Context
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
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.RelativeLayout
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
    private var promoted = false
    private var amountPaid = 0L

    private lateinit var addGuestBottomSheet: LinearLayout
    private lateinit var addGuestLayout: LinearLayout
    private lateinit var promoteEvent: RelativeLayout

    private lateinit var fireStoreRepo: FireStoreRepo
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event_final)

        addGuestBottomSheet = findViewById(R.id.add_guest_bsheet)
        addGuestLayout = findViewById(R.id.add_event_final_guest_container)
        promoteEvent = findViewById(R.id.add_event_final_promote_event_container)
        promoteEvent.setOnClickListener {

            val intent = Intent(this, PromoteEventActivity::class.java)
            startActivityForResult(intent, 2030)
        }

        specialGuest = HashMap()
        fireStoreRepo = FireStoreRepo()

        bottomSheetBehavior = BottomSheetBehavior.from(addGuestBottomSheet)
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                add_event_final_bottom_sheet_bg.visibility = VISIBLE
                add_event_final_bottom_sheet_bg.alpha = slideOffset
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED){
                    add_event_final_bottom_sheet_bg.visibility = GONE
                }
            }
        })

        add_guest_bsheet_close.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        add_event_final_add_guest_container.setOnClickListener {
            closeKeyboard()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        add_event_final_bottom_sheet_bg.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
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

        val bundle = intent.extras!!

        eventTitle = bundle.getString("eventTitle")!!
        eventDesc = bundle.getString("eventDesc")!!
        eventDate = bundle.getString("eventDate")!!
        eventLocation = bundle.getString("eventLocation")!!
        eventTag = bundle.getString("eventTag")!!
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
            val eventTicketLink = add_event_final_ticket_edit.text.toString().trim()

            val eventModel = EventsModel(eventType = eventType, eventTitle = eventTitle,
                eventDesc = eventDesc, eventDate = eventDate, eventImageLink = eventPicUri.toString(), eventImageLinkThumb = "",
                eventLocation = eventLocation, eventDressCode = eventDressCode, eventRegLink = eventRegLink, eventTags = listOf(eventTag),
                eventMilis = eventMillis, eventGuests = specialGuest, eventPostTime = eventPostTime.toString(), eventTicketLink = eventTicketLink,
                userUID = AuthRepo.getUserUid(),amountPaid = amountPaid)

            fireStoreRepo.postEvent(this, eventModel, {

                val v = findViewById<View>(android.R.id.content)
                AppUtils.getCustomSnackBar(v, "Your event has been shared!", this).show()

                startActivity(Intent(this, HomeActivity::class.java))
                finish()
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
        val eventTicketLink = add_event_final_ticket_edit.text.toString().trim()

        if(eventDressCode.isNotEmpty() || eventRegLink.isNotEmpty() || specialGuest.isNotEmpty() || eventTicketLink.isNotEmpty()){
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK){
            if (requestCode == 2030){
                if (data != null){
                    val paymentStatus = data.extras!!.getString("paymentStatus")
                    val amountPaidFromExtra = data.extras!!.getLong("promotedEvent")
                    amountPaid = amountPaidFromExtra
                    if(paymentStatus == "success"){
                        when(amountPaidFromExtra){

                            1000L -> {
                                eventType = "promoted"

                                add_event_final_promote_event_container.background = ContextCompat.getDrawable(this, R.drawable.buttonbg)
                                add_event_final_promote_event_text.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                                add_event_final_promote_event_text.text = ("Big Audience Plan: N1,000.000")
                            }

                            5000L -> {
                                eventType = "promoted"
                                add_event_final_promote_event_container.background = ContextCompat.getDrawable(this, R.drawable.buttonbg)
                                add_event_final_promote_event_text.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                                add_event_final_promote_event_text.text = ("Medium Audience Plan: N5,000.000")
                            }

                            10000L -> {
                                eventType = "promoted"
                                add_event_final_promote_event_container.background = ContextCompat.getDrawable(this, R.drawable.buttonbg)
                                add_event_final_promote_event_text.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                                add_event_final_promote_event_text.text = ("Large Audience Plan: N10,000.000")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun closeKeyboard(){
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = currentFocus
        if (view == null){
            view = View(this)
        }

        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
