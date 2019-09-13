package com.exceptos.tukio.View.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.exceptos.tukio.Data.ModelClasses.GuestModel
import com.exceptos.tukio.Data.ModelClasses.breakDocumentIntoEvntsModel
import com.exceptos.tukio.R
import com.exceptos.tukio.Repos.AuthRepo
import com.exceptos.tukio.Repos.FireStoreRepo
import com.exceptos.tukio.Services.GoogleServicesClass
import com.exceptos.tukio.Utils.AppUtils
import com.exceptos.tukio.Utils.IntentUtils
import kotlinx.android.synthetic.main.activity_event_details.*

class EventDetailsActivity : AppCompatActivity() {

    private lateinit var eventTitleText: TextView
    private lateinit var eventLocationText: TextView
    private lateinit var eventDateText: TextView
    private lateinit var eventDescriptionText: TextView
    private lateinit var eventDressCodeText: TextView
    private lateinit var eventRegLinkText: TextView
    private lateinit var specialGuestsRecycler: RecyclerView
    private lateinit var noSpecialGuests: TextView
    private lateinit var eventImageView: ImageView
    private lateinit var backBtn: ImageButton
    private lateinit var viewImageText: TextView
    private lateinit var promotedLabel: RelativeLayout

    private lateinit var reminderBtn: ImageView
    private lateinit var commentBtn: ImageView
    private lateinit var shareBtn: ImageView
    private lateinit var moreDetailsBtn: ImageView
    private lateinit var updatesBtn: ImageView

    private lateinit var shareBottomSheet: LinearLayout
    private lateinit var bottomSheetBehaviour: BottomSheetBehavior<LinearLayout>
    private lateinit var copyEventLink: LinearLayout
    private lateinit var shareEventSocial: LinearLayout
    private lateinit var shareEventTukpic: LinearLayout
    private lateinit var closeShareBottomSheet: ImageButton

    private lateinit var documentID: String
    private lateinit var posterUID: String
    private lateinit var eventLocation: String
    private lateinit var eventTitle: String
    private lateinit var eventDate: String
    private lateinit var eventDesc: String
    private lateinit var eventDressCode: String
    private lateinit var eventRegLink: String
    private lateinit var eventImageLink: String
    private lateinit var eventImageThumbLink: String
    private lateinit var eventTicketLink: String
    private var eventMilis: Long? = null

    private lateinit var fireStoreRepo: FireStoreRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY)
        setContentView(R.layout.activity_event_details)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        eventTitleText = findViewById(R.id.event_details_event_title)
        promotedLabel = findViewById(R.id.event_details_promoted_label)
        eventLocationText = findViewById(R.id.event_details_location)
        eventDateText = findViewById(R.id.event_details_date)
        eventDescriptionText = findViewById(R.id.event_details_description)
        eventDressCodeText = findViewById(R.id.event_details_dress_code)
        eventRegLinkText = findViewById(R.id.event_details_reg_link)
        noSpecialGuests = findViewById(R.id.event_details_no_special_guests_text)
        specialGuestsRecycler = findViewById(R.id.event_details_guest_recycler)
        eventImageView = findViewById(R.id.event_details_event_image)
        backBtn = findViewById(R.id.event_details_back)
        viewImageText = findViewById(R.id.event_details_view_image_text)
        backBtn.setOnClickListener {
            finish()
        }

        specialGuestsRecycler.setHasFixedSize(true)
        specialGuestsRecycler.layoutManager = LinearLayoutManager(this)

        reminderBtn = findViewById(R.id.event_details_reminder_btn)
        commentBtn = findViewById(R.id.event_details_comment)
        shareBtn = findViewById(R.id.event_details_share_btn)
        moreDetailsBtn = findViewById(R.id.event_details_more_details)
        updatesBtn = findViewById(R.id.event_details_update)

        shareBottomSheet = findViewById(R.id.event_details_share_bottomsheet)
        bottomSheetBehaviour = BottomSheetBehavior.from(shareBottomSheet)
        copyEventLink = findViewById(R.id.share_bsheet_copylink)
        shareEventSocial = findViewById(R.id.share_bsheet_socialmedia)
        shareEventTukpic = findViewById(R.id.share_bsheet_tukpic)
        closeShareBottomSheet = findViewById(R.id.share_bsheet_close)

        val bundle = intent.extras
        fireStoreRepo = FireStoreRepo()
        bindData(bundle!!)

        bottomSheetBehaviour.setBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback(){
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                event_details_bottom_sheet_bg.visibility = VISIBLE
                event_details_bottom_sheet_bg.alpha = slideOffset
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED){
                    event_details_bottom_sheet_bg.visibility = GONE
                }
           }
        })

        event_details_bottom_sheet_bg.setOnClickListener {
            bottomSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        shareBtn.setOnClickListener {
            bottomSheetBehaviour.state = BottomSheetBehavior.STATE_EXPANDED
        }

        commentBtn.setOnClickListener {
            val intent = Intent(this, CommentActivity::class.java)
            intent.putExtra("eventID", documentID)
            startActivity(intent)
        }

        updatesBtn.setOnClickListener {
            val intent = Intent(this, UpdatesActivity::class.java)
            intent.putExtra("documentID", documentID)
            intent.putExtra("posterID",posterUID)
            startActivity(intent)

        }

        moreDetailsBtn.setOnClickListener {

        }

        shareEventTukpic.setOnClickListener {
            //TODO: INSERT TUKPIC FEATURE HERE
        }

        closeShareBottomSheet.setOnClickListener {
            bottomSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        copyEventLink.setOnClickListener {
            AppUtils.copyTextToClipBoard(AppUtils.createEventLink(documentID), this)
            bottomSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        shareEventSocial.setOnClickListener {
            IntentUtils.shareEvent(context = this, eventDate = eventDate, eventLocation = eventLocation,
                eventTitle = eventTitle, eventID = documentID)
            bottomSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        viewImageText.setOnClickListener {
            val intent = Intent(this, ImageViewingActivity::class.java)
            intent.putExtra("eventImage", eventImageLink)
            intent.putExtra("eventImageThumb", eventImageThumbLink)
            startActivity(intent)
        }

    }

    private fun bindData(bundle: Bundle){

        val eventType = bundle.getString("eventType")
        documentID = bundle.getString("documentID")!!
        posterUID = bundle.getString("posterUID")!!
        eventDate = bundle.getString("eventDate")!!
        eventLocation = bundle.getString("eventLocation")!!
        eventTitle = bundle.getString("eventTitle")!!
        eventDesc = bundle.getString("eventDesc")!!
        eventDressCode = bundle.getString("eventDressCode")!!
        eventRegLink = bundle.getString("eventRegLink")!!
        eventImageLink = bundle.getString("eventImageLink")!!
        eventImageThumbLink = bundle.getString("eventImageThumb")!!
        eventMilis = bundle.getLong("eventMilis")
        eventTicketLink = bundle.getString("eventTicketLink")!!

        if (eventType ==  "promoted")promotedLabel.visibility = VISIBLE
        if(eventType == "promoted" && posterUID == AuthRepo.getUserUid()){
            moreDetailsBtn.visibility = GONE
        }

        Log.e(EventDetailsActivity::class.java.simpleName, eventMilis.toString())

        eventDateText.text = eventDate
        eventTitleText.text = eventTitle
        event_details_ticket_link.text = if (eventTicketLink.isNotEmpty()) eventTicketLink else "There is no ticketing platform for this event"
        eventDescriptionText.text = eventDesc
        eventDressCodeText.text = if (eventDressCode.isNotEmpty()) eventDressCode else "There is no dress code for this event!"
        eventRegLinkText.text = if (eventRegLink.isNotEmpty()) eventRegLink else "There is no online registration for this event!"
        eventLocationText.text = eventLocation
        eventLocationText.setOnClickListener {
            GoogleServicesClass.startLocationActivity(eventLocation, this)
        }

        eventRegLinkText.setOnClickListener {
            val packageName = CustomTabsHelper.getPackageNameToUse(this)

            if (eventRegLink.isNotEmpty()){

                if ( packageName != null ) {

                    val url = if(!eventRegLink.contains("http")&& !eventRegLink.contains("https"))"http://$eventRegLink" else eventRegLink
                    val builder = CustomTabsIntent.Builder()
                    builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    builder.setShowTitle(true)

                    val intent = builder.build()
                    intent.intent.setPackage(packageName)
                    intent.launchUrl(this, Uri.parse(url))
                }else{

                    val url = if(!eventRegLink.contains("http")&& !eventRegLink.contains("https"))"http://$eventRegLink" else eventRegLink
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    val chooser = Intent.createChooser(intent, "Open url using..")
                    startActivity(chooser)
                }
            }
        }

        event_details_ticket_link.setOnClickListener {
            val packageName = CustomTabsHelper.getPackageNameToUse(this)

            if (eventTicketLink.isNotEmpty()){

                if ( packageName != null ) {

                    val url = if(!eventTicketLink.contains("http")&& !eventTicketLink.contains("https"))"http://$eventTicketLink" else eventTicketLink
                    val builder = CustomTabsIntent.Builder()
                    builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    builder.setShowTitle(true)

                    val intent = builder.build()
                    intent.intent.setPackage(packageName)
                    intent.launchUrl(this, Uri.parse(url))
                }else{

                    val url = if(!eventTicketLink.contains("http")&& !eventTicketLink.contains("https"))"http://$eventTicketLink" else eventTicketLink
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    val chooser = Intent.createChooser(intent, "Open url using..")
                    startActivity(chooser)
                }
            }
        }

        reminderBtn.visibility = GONE


        val requestOptions = RequestOptions()
        requestOptions.placeholder(ContextCompat.getDrawable(this, R.drawable.placeholder_2))

        val thumbNailRequest = Glide.with(this).load(eventImageThumbLink)
        Glide.with(this.applicationContext)
            .setDefaultRequestOptions(requestOptions)
            .asDrawable()
            .load(eventImageLink)
            .thumbnail(thumbNailRequest)
            .into(eventImageView)

        fireStoreRepo.getEventDocumentRefererenceById(documentID).addSnapshotListener { snapshot, _ ->
            if (snapshot!= null && snapshot.exists()){
                val eventModel = snapshot.breakDocumentIntoEvntsModel()
                val mapOfGuests = eventModel.eventGuests

                if ( mapOfGuests != null && mapOfGuests.isNotEmpty()){
                    val listOfGuest = mutableListOf<GuestModel>()
                    for (name in mapOfGuests.keys){
                        val bio = mapOfGuests[name].toString()

                        val guest = GuestModel(name, bio)
                        listOfGuest.add(guest)
                    }

                    val adapter = com.exceptos.tukio.Data.Adapters.GuestsAdapter(this, listOfGuest)
                    specialGuestsRecycler.adapter = adapter
                    specialGuestsRecycler.visibility = VISIBLE
                    noSpecialGuests.visibility = GONE


                }else{
                    specialGuestsRecycler.visibility = GONE
                    noSpecialGuests.visibility = VISIBLE
                }
            }
        }

    }
}
