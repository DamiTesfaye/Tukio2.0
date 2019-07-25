package io.neolution.eventify.View.Activities

import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.VISIBLE
import com.bumptech.glide.Glide
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.Places
import io.neolution.eventify.Data.Adapters.GuestsAdapter
import io.neolution.eventify.Data.ModelClasses.EventsModel
import io.neolution.eventify.Data.ModelClasses.FullEventsModel
import io.neolution.eventify.Data.ModelClasses.GuestModel
import io.neolution.eventify.Data.ModelClasses.initRecyclerView
import io.neolution.eventify.R
import io.neolution.eventify.Repos.AuthRepo
import io.neolution.eventify.Repos.FireStoreRepo
import io.neolution.eventify.Services.GoogleServicesClass
import io.neolution.eventify.Utils.AppUtils
import io.neolution.eventify.Utils.DateFormatterUtil
import io.neolution.eventify.Utils.IntentUtils
import kotlinx.android.synthetic.main.activity_full_details.*
import kotlinx.android.synthetic.main.activity_full_details_from_link.*


class FullDetailsFromLinkActivity : AppCompatActivity(),
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener , View.OnClickListener{
    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.activity_full_details_from_link_comment_button -> {
                val intent = Intent(this, CommentActivity::class.java)
                intent.putExtra("eventID", documentID)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
                }else{
                    startActivity(intent)
                }
            }

            R.id.activity_full_details_from_link_going_btn -> {
                fireStoreRepo.likeDislikeEvent(this, documentID, v)
            }

            R.id.activity_full_details_from_link_event_reg_link_tv -> {

                val regLink = activity_full_details_from_link_event_reg_link_tv.text
                val packageName = CustomTabsHelper.getPackageNameToUse(this)

                if (!regLink.isEmpty()){

                    if ( packageName != null ) {


                        val url = ("http://$regLink")

                        val builder = CustomTabsIntent.Builder()
                        builder.setToolbarColor(ContextCompat.getColor(this, io.neolution.eventify.R.color.colorPrimary))

                        val intent = builder.build()
                        intent.intent.setPackage(packageName)
                        intent.launchUrl(this, Uri.parse(url))
                    }else{
                        val url = ("http://$regLink")

                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        val chooser = Intent.createChooser(intent, "Open url using..")
                        startActivity(chooser)
                    }
                }


            }

            R.id.activity_full_details_from_link_event_ticket_link_tv -> {

                val ticketLink = activity_full_details_from_link_event_ticket_link_tv.text
                val packageName = CustomTabsHelper.getPackageNameToUse(this)

                if (ticketLink.isNotEmpty()) {

                    if ( packageName != null ) {

                        //TODO: TEST CHANGES AND BUILD NEW RELEASE APP BUNDLE..
                        val url = ("http://$ticketLink")

                        val builder = CustomTabsIntent.Builder()

                        builder.setToolbarColor(ContextCompat.getColor(this, io.neolution.eventify.R.color.colorPrimary))

                        val intent = builder.build()
                        intent.intent.setPackage(packageName)
                        intent.launchUrl(this, Uri.parse(url))
                    }else{
                        val url = ("http://$ticketLink")

                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        val chooser = Intent.createChooser(intent, "Open url using..")
                        startActivity(chooser)
                    }


                }
            }
        }
    }

    override fun onConnected(p0: Bundle?) {
        Log.e(HomeActivity::class.java.simpleName, p0.toString())
    }

    override fun onConnectionSuspended(p0: Int) {
        Log.e(HomeActivity::class.java.simpleName, p0.toString())
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.e(HomeActivity::class.java.simpleName, p0.toString())
    }

    private lateinit var posterUID: String
    private lateinit var eventLocation: String
    private lateinit var eventTitle: String
    private lateinit var eventDate: String
    private lateinit var documentID: String
    private lateinit var fireStoreRepo: FireStoreRepo

    lateinit var instanceEvents: FullEventsModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_details_from_link)

        val dialog = AppUtils.instantiateProgressDialog("Retrieving your event", this)
        dialog.show()

         fireStoreRepo = FireStoreRepo()



        activity_full_details__from_link_close_image_btn.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()

        }


        val appLinkIntent = intent
        val appLinkData = appLinkIntent.data

        if(appLinkIntent != null && appLinkData.lastPathSegment != null){

            documentID = appLinkData.lastPathSegment
            fireStoreRepo.getEventDocumentRefererenceById(documentID).addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()){

                    val eventModel = snapshot.toObject(EventsModel::class.java)
                    instanceEvents = FullEventsModel(eventModel!!, snapshot.id)
                    val currentEvents = FullEventsModel(eventModel, snapshot.id)

                    val eventType = currentEvents.eventsModel.eventType
                    posterUID = currentEvents.eventsModel.userUID

                    if (eventType == "promoted" && posterUID == AuthRepo.getUserUid()){
                        activity_full_details_from_link_seen_btn.visibility = VISIBLE
                        activity_full_details_from_link_seen_count.visibility = VISIBLE

                        fireStoreRepo.getPromotedEventClickedCollection(documentID).addSnapshotListener { snapshot2, _ ->
                            if (snapshot2 != null && !snapshot2.isEmpty){
                                activity_full_details_from_link_seen_count.text = snapshot2.size().toString()
                            }else{
                                activity_full_details_from_link_seen_count.text = "0"
                            }
                        }
                    }

                    activity_full_details_from_link_event_location_tv
                        .text = currentEvents.eventsModel.eventLocation

                    activity_full_details_from_link_event_location_tv.setOnClickListener {
                        GoogleServicesClass.startLocationActivity(currentEvents.eventsModel.eventLocation, this)
                    }

                    activity_full_details_from_link_event_desc_tv.text = currentEvents.eventsModel.eventDesc

                    val imageView = activity_full_details__from_link_event_poster
                    val thumbNailRequest = Glide.with(this).load(currentEvents.eventsModel.eventImageLinkThumb)

                    Glide.with(this.applicationContext)
                        .asDrawable()
                        .load(currentEvents.eventsModel.eventImageLink)
                        .thumbnail(thumbNailRequest)
                        .into(imageView)

                    eventTitle = currentEvents.eventsModel.eventTitle
                    activity_full_details_from_link_event_time_tv.text = currentEvents.eventsModel.eventDate
                    eventLocation = currentEvents.eventsModel.eventLocation


                   activity_full_details_from_link_event_title_tv.text = eventTitle

                    activity_full_details_from_link_event_ticket_link_tv.run {
                        text = currentEvents.eventsModel.eventTicketLink
                        setOnClickListener(this@FullDetailsFromLinkActivity)
                    }
                    activity_full_details_from_link_event_reg_link_tv.run {
                        text = currentEvents.eventsModel.eventRegLink
                        setOnClickListener(this@FullDetailsFromLinkActivity)
                    }

                    activity_full_details_from_link_event_dress_code_tv.text = currentEvents.eventsModel.eventDressCode
                    activity_full_details_from_link_event_guest_speakers.initRecyclerView(AppUtils.LINEAR_LAYOUT_MANAGER, this)

                    val mapOfGuests = currentEvents.eventsModel.eventGuests

                    if ( mapOfGuests != null && !mapOfGuests.isEmpty()){
                        val listOfGuest = mutableListOf<GuestModel>()
                        for (name in mapOfGuests.keys){
                            val bio = mapOfGuests[name].toString()

                            val guest = GuestModel(name, bio)
                            listOfGuest.add(guest)
                        }

                        val adapter = GuestsAdapter(this, listOfGuest)
                        activity_full_details_from_link_event_guest_speakers.adapter = adapter

                    }

                    dialog.dismiss()
                }
            }

            activity_full_details_from_link_comment_button.setOnClickListener(this)
            activity_full_details_from_link_going_btn.setOnClickListener(this)

            fireStoreRepo.getDocumentLikesCollection(documentID).document(AuthRepo.getUserUid())
                .get().addOnSuccessListener {
                    if (it.exists()){
                        activity_full_details_from_link_going_btn.
                            setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_going_two))
                    }else{
                        activity_full_details_from_link_going_btn
                            .setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_going))
                    }
                }


            fireStoreRepo.getDocumentLikesCollection(documentID)
                .addSnapshotListener { snapshot, _ ->

                    if (snapshot!= null && !snapshot.isEmpty  ){
                        activity_full_details_from_link_going_count.text = snapshot.size().toString()
                    }else{
                        activity_full_details_from_link_going_count.text = "0"
                    }

                }
            fireStoreRepo.getDocumentCommentCollection(documentID).addSnapshotListener { snapshot, _ ->
                if (snapshot!= null && !snapshot.isEmpty  ){
                    activity_full_details_from_link_comment_count.text = snapshot.size().toString()
                }else{
                    activity_full_details_from_link_comment_count.text = "0"
                }
            }




        }



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.full_details_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId){
            R.id.full_details_action_share -> {

                IntentUtils.shareEvent(context = this, eventTitle =eventTitle,
                    eventLocation =  eventLocation,
                    eventDate = eventDate,
                    eventID = documentID)

                true
            }
            R.id.full_details_action_updates -> {
                val intent = Intent(this, UpdatesActivity::class.java)
                intent.putExtra("documentID",documentID)
                intent.putExtra("posterID",posterUID)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
                }else{
                    startActivity(intent)
                }

                true
            }

            else ->{
                return super.onOptionsItemSelected(item)
            }

        }
    }

    override fun onStart() {
        super.onStart()

//        loadPlaceNameAddress(instanceEvents.eventsModel.eventLocation.toString())
    }

    override fun onBackPressed() {
        super.onBackPressed()

        startActivity(Intent(this, HomeActivity::class.java))
    }
}
