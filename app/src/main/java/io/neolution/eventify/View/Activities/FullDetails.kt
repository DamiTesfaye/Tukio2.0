package io.neolution.eventify.View.Activities

import CustomTabsHelper
import android.app.ActivityOptions
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.VISIBLE
import com.bumptech.glide.Glide
import io.neolution.eventify.Data.Adapters.GuestsAdapter
import io.neolution.eventify.Data.ModelClasses.GuestModel
import io.neolution.eventify.Data.ModelClasses.breakDocumentIntoEvntsModel
import io.neolution.eventify.Repos.AuthRepo
import io.neolution.eventify.Repos.FireStoreRepo
import io.neolution.eventify.R
import io.neolution.eventify.Utils.IntentUtils
import io.neolution.eventify.databinding.ActivityFullDetailsBinding
import kotlinx.android.synthetic.main.activity_full_details.*


class FullDetails : AppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View?) {
        when (v!!.id){
            R.id.activity_full_details_close_image_btn -> {
                onBackPressed()
            }

            R.id.activity_full_details_comment_button -> {
                val intent = Intent(this, CommentActivity::class.java)
                intent.putExtra("eventID",documentID)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
                }else{
                    startActivity(intent)
                }
            }

            R.id.activity_full_details_going_btn -> {
                fireStoreRepo.likeDislikeEvent(this, documentID,  v)
            }

            io.neolution.eventify.R.id.activity_full_details_event_ticket_link_tv -> {

                val packageName = CustomTabsHelper.getPackageNameToUse(this)


                val ticketLink = binding.activityFullDetailsEventTicketLinkTv.text

                if (ticketLink.isNotEmpty()) {

                    if ( packageName != null ) {


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

            R.id.activity_full_details_event_reg_link_tv -> {

                val packageName = CustomTabsHelper.getPackageNameToUse(this)
                val regLink = binding.activityFullDetailsEventRegLinkTv.text

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
        }
    }

    private lateinit var binding: ActivityFullDetailsBinding
    private lateinit var documentID: String
    private lateinit var posterUID: String
    private lateinit var eventLocation: String
    private lateinit var eventTitle: String
    private lateinit var eventDate: String
    private lateinit var fireStoreRepo: FireStoreRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_full_details)

        val bundle = intent.extras
        documentID = bundle.getString("documentID")
        bindData(bundle)

        fireStoreRepo = FireStoreRepo()

        binding.activityFullDetailsCloseImageBtn.setOnClickListener(this)
        activity_full_details_going_btn.setOnClickListener (this)
        activity_full_details_comment_button.setOnClickListener (this)

        binding.activityFullDetailsEventRegLinkTv.setOnClickListener(this)
        binding.activityFullDetailsEventTicketLinkTv.setOnClickListener(this)



        fireStoreRepo.getDocumentLikesCollection(documentID)
            .addSnapshotListener { snapshot, _ ->

                if (snapshot!= null && !snapshot.isEmpty  ){
                    activity_full_details_going_count.text = snapshot.size().toString()
                }else{
                    activity_full_details_going_count.text = "0"
                }

            }
        fireStoreRepo.getDocumentCommentCollection(documentID).addSnapshotListener { snapshot, _ ->
            if (snapshot!= null && !snapshot.isEmpty  ){
                activity_full_details_comment_count.text = snapshot.size().toString()
            }else{
                activity_full_details_comment_count.text = "0"
            }
        }

        fireStoreRepo.getDocumentLikesCollection(documentID).document(AuthRepo.getUserUid())
            .addSnapshotListener(this) {snapshot, _ ->
                if (snapshot != null && snapshot.exists()){
                    activity_full_details_going_btn.
                        setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_going_two))
                }else{
                    activity_full_details_going_btn
                        .setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_going))
                }
            }

    }

    fun bindData(bundle: Bundle){

        posterUID = bundle.getString("posterUID")

        val manager = LinearLayoutManager(this)
        binding.activityFullDetailsEventGuestSpeakers.apply{
            setHasFixedSize(true)
            layoutManager = manager
        }

        val eventType = bundle.getString("eventType")
        if(eventType == "promoted" && posterUID == AuthRepo.getUserUid()){
            activity_full_details_seen_btn.visibility = VISIBLE
            activity_full_details_seen_count.visibility = VISIBLE

            FireStoreRepo().getPromotedEventClickedCollection(documentID).addSnapshotListener { snapshot, _ ->
                if (snapshot != null && !snapshot.isEmpty){
                    activity_full_details_seen_count.text = snapshot.size().toString()
                }else{
                    activity_full_details_seen_count.text = "0"
                }
            }
        }

        FireStoreRepo().getEventDocumentRefererenceById(documentID).addSnapshotListener { snapshot, exception ->
            if (snapshot!= null && snapshot.exists()){
                val eventModel = snapshot.breakDocumentIntoEvntsModel()
                val mapOfGuests = eventModel.eventGuests

                if ( mapOfGuests != null && !mapOfGuests.isEmpty()){
                        val listOfGuest = mutableListOf<GuestModel>()
                        for (name in mapOfGuests.keys){
                            val bio = mapOfGuests[name].toString()

                            val guest = GuestModel(name, bio)
                            listOfGuest.add(guest)
                        }

                        val adapter = GuestsAdapter(this, listOfGuest)
                        binding.activityFullDetailsEventGuestSpeakers.adapter = adapter

                }
            }
        }

        eventDate = bundle.getString("eventDate")
        eventLocation = bundle.getString("eventLocation")
        eventTitle = bundle.getString("eventTitle")

        binding.activityFullDetailsEventTitleTv.text = eventTitle
        binding.activityFullDetailsEventDescTv.text = bundle.getString("eventDesc")
        binding.activityFullDetailsEventDressCodeTv.text = bundle.getString("eventDressCode")
        binding.activityFullDetailsEventLocationTv.text = eventLocation
        binding.activityFullDetailsEventRegLinkTv.text = bundle.getString("eventRegLink")
        binding.activityFullDetailsEventTicketLinkTv.text = bundle.getString("eventTicketLink")
        binding.activityFullDetailsEventTimeTv.text = eventDate

        val picLink = bundle.getString("eventImageLink")
        val thumbNailRequest = Glide.with(this).load(picLink)
        Glide.with(this.applicationContext)
            .asDrawable()
            .load(picLink)
            .thumbnail(thumbNailRequest)
            .into(binding.activityFullDetailsEventPoster )


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        MenuInflater(this).inflate(R.menu.full_details_menu, menu)
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
}
