package io.neolution.eventify.Data.Adapters

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FieldValue
import de.hdodenhof.circleimageview.CircleImageView
import io.neolution.eventify.Data.ModelClasses.FullEventsModel
import io.neolution.eventify.Data.ModelClasses.breakDownToUserModel
import io.neolution.eventify.Data.ModelClasses.initRecyclerView
import io.neolution.eventify.Data.ModelClasses.passIntoIntent
import io.neolution.eventify.Listeners.OnShareEventClicked
import io.neolution.eventify.R
import io.neolution.eventify.Repos.FireStoreRepo
import io.neolution.eventify.Services.GoogleServicesClass
import io.neolution.eventify.Utils.AppUtils
import io.neolution.eventify.Utils.DateFormatterUtil
import io.neolution.eventify.Utils.FirebaseUtils
import io.neolution.eventify.Utils.IntentUtils
import io.neolution.eventify.View.Activities.CommentActivity
import io.neolution.eventify.View.Activities.FullDetails
import io.neolution.eventify.View.Activities.UpdatesActivity

/**
 * Created by Big-Nosed Developer on the Edge of Infinity.
 */
class HomeAdapter(val context: Context,
                  val listOfEvents: List<FullEventsModel>,val activity: FragmentActivity, var shareEventListener: OnShareEventClicked)
    : RecyclerView.Adapter<HomeAdapter.HomeViewholder>() {

    private val fireStoreRepo = FireStoreRepo()

    class HomeViewholder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeAdapter.HomeViewholder {
        val view = LayoutInflater.from(context).inflate(R.layout.new_event_viewholder, parent, false)
        return HomeViewholder(view)
    }

    override fun getItemCount() = listOfEvents.size

    override fun onBindViewHolder(holder: HomeAdapter.HomeViewholder, position: Int) {

        val currentEvents = listOfEvents[position]
        val userInfo = fireStoreRepo.getUserAccountDetailsFromUid(currentEvents.eventsModel.userUID)

        var userName = ""

        userInfo.addSnapshotListener(activity) { snapshot, _ ->
            if (snapshot != null && snapshot.exists()){
                   val userModel = snapshot.breakDownToUserModel()
                userName = userModel.userName
                val userPicLink = userModel.userImage

                holder.itemView.findViewById<TextView>(R.id.new_event_vh_username).background = ContextCompat.getDrawable(context, R.drawable.transparent_drawable)
                holder.itemView.findViewById<TextView>(R.id.new_event_vh_username).text = userName


                val imageView = holder.itemView.findViewById<CircleImageView>(R.id.new_event_vh_user_image)

                val requestOptions = RequestOptions()
                requestOptions.placeholder(ContextCompat.getDrawable(context, R.drawable.placeholder))
                val thumbNailRequest = Glide.with(context.applicationContext).load(userModel.userThumbLink)

                Glide.with(context.applicationContext)
                    .setDefaultRequestOptions(requestOptions)
                    .asDrawable()
                    .load(userPicLink)
                    .thumbnail(thumbNailRequest)
                    .into(imageView)

            }
        }

        val promotedTag = holder.itemView.findViewById<TextView>(R.id.new_event_vh_promoted_tag)

        if (currentEvents.eventsModel.eventType == "promoted"){
            promotedTag.visibility = VISIBLE
        }else{
            promotedTag.visibility = GONE
        }

        holder.itemView.findViewById<TextView>(R.id.new_event_vh_location_text)
            .text = currentEvents.eventsModel.eventLocation


        holder.itemView.findViewById<ImageView>(R.id.new_event_vh_share_event).setOnClickListener {
//
//      //TODO: SHOULD POP OUT A BOTTOM SHEET ASKING IF THEY WANT TO USE TUK PIC OR JUST SHARING NORMALLY
// IntentUtils.shareEvent(context = context, eventTitle = currentEvents.eventsModel.eventTitle,
//                eventLocation =  currentEvents.eventsModel.eventLocation,
//                eventDate = currentEvents.eventsModel.eventDate,
//                eventID =currentEvents.eventID)

            shareEventListener.onShareButtonClick(eventTitle = currentEvents.eventsModel.eventTitle,
                eventLocation = currentEvents.eventsModel.eventLocation, eventDate = currentEvents.eventsModel.eventDate,
                eventId = currentEvents.eventID)
        }

//        holder.itemView.findViewById<TextView>(R.id.explore_viewholder_event_location_see_directions).setOnClickListener {
//
//            GoogleServicesClass.startLocationActivity(currentEvents.eventsModel.eventLocation, context)
//        }


        val imageView = holder.itemView.findViewById<ImageView>(R.id.new_event_vh_event_image)
        val requestOptions = RequestOptions()
        requestOptions.placeholder(ContextCompat.getDrawable(context, R.drawable.eventify_placeholder))

        val thumbNailRequest = Glide.with(context).load(currentEvents.eventsModel.eventImageLinkThumb)
        Glide.with(context.applicationContext)
            .setDefaultRequestOptions(requestOptions)
            .asDrawable()
            .thumbnail(thumbNailRequest)
            .load(currentEvents.eventsModel.eventImageLink)
            .into(imageView)

//        holder.itemView.findViewById<CardView>(R.id.event_poster_container).setOnClickListener {
//            if ( userName != "" ){
//
//                currentEvents.eventsModel.eventType?.let {eventType ->
//                    if (eventType == "promoted"){
//
//                        if (currentEvents.eventsModel.userUID != FirebaseUtils().getUserUID(context)){
//                            fireStoreRepo.getPromotedEventClickedCollection(currentEvents.eventID).document(FirebaseUtils().getUserUID(context))
//                                .get().addOnSuccessListener { document ->
//                                    if (document != null && document.exists()){
//
//                                    }else{
//
//                                        val timeStamp = FieldValue.serverTimestamp()
//                                        val hashMap = HashMap<String, Any>()
//                                        hashMap["timestamp"] = timeStamp
//
//                                        fireStoreRepo.getPromotedEventClickedCollection(currentEvents.eventID).document(FirebaseUtils().getUserUID(context)).set(hashMap)
//                                    }
//
//                                }
//                        }
//
//
//                    }
//                }
//
//                val intent = listOfEvents[position].passIntoIntent(context, FullDetails::class.java, eventLocation = currentEvents.eventsModel.eventLocation
//                    , userName = userName, startedFrom = this.javaClass.name)
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle())
//                }else{
//                    context.startActivity(intent)
//                }
//            }
//        }

//        holder.itemView.findViewById<TextView>(R.id.explore_viewholder_view_full_details_tv).setOnClickListener {
//
//            if ( userName != "" ){
//
//                currentEvents.eventsModel.eventType?.let {eventType ->
//                    if (eventType == "promoted"){
//
//                        if (currentEvents.eventsModel.userUID != FirebaseUtils().getUserUID(context)){
//                            fireStoreRepo.getPromotedEventClickedCollection(currentEvents.eventID).document(FirebaseUtils().getUserUID(context))
//                                .get().addOnSuccessListener { document ->
//                                    if (document != null && document.exists()){
//
//                                    }else{
//
//                                        val timeStamp = FieldValue.serverTimestamp()
//                                        val hashMap = HashMap<String, Any>()
//                                        hashMap["timestamp"] = timeStamp
//
//                                        fireStoreRepo.getPromotedEventClickedCollection(currentEvents.eventID).document(FirebaseUtils().getUserUID(context)).set(hashMap)
//                                    }
//
//                            }
//                        }
//
//
//                    }
//                }
//
//                val intent = listOfEvents[position].passIntoIntent(context, FullDetails::class.java, eventLocation = currentEvents.eventsModel.eventLocation
//                    , userName = userName, startedFrom = this.javaClass.name)
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle())
//                }else{
//                    context.startActivity(intent)
//                }
//            }
//
//        }

        holder.itemView.findViewById<TextView>(R.id.new_event_vh_event_title_text).text = currentEvents.eventsModel.eventTitle
        holder.itemView.findViewById<TextView>(R.id.new_event_vh_date_text).text = currentEvents.eventsModel.eventDate

        val updateImageButton = holder.itemView.findViewById<ImageView>(R.id.new_event_vh_event_updates)

        fireStoreRepo.getDocumentUpdatesPath(currentEvents.eventID).addSnapshotListener {
                snapshot, _ ->

            if(snapshot != null && !snapshot.isEmpty){
                updateImageButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_notification_added))
            }else{
                updateImageButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_notification))

            }

        }

        updateImageButton.setOnClickListener {

            val intent = Intent(context, UpdatesActivity::class.java)
            intent.putExtra("documentID", currentEvents.eventID)
            intent.putExtra("posterID",currentEvents.eventsModel.userUID)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle())
            }else{
                context.startActivity(intent)
            }
        }

        // val chipList = mutableListOf("Entertainment", "Sports",
        ////            "Tech", "Movements", "MeetUps" , "Business",
        ////            "Concerts", "Conventions/Conferences", "Parties", "Galas", "Motivation",
        ////            "Skills and Acquisitions", "Workshops/Seminars",
        ////            "Meetings", "Birthdays", "Sports",
        ////             "Christian", "Muslim", "Football",
        ////            "Basketball", "Athletics",
        ////            "School", "Convocations", "Valedictory Service" , "Relationship",
        ////            "Festivals", "Tests", "Examination", "Extra-Credits",
        ////            "Send-Offs")

        val eventTagImage = holder.itemView.findViewById<ImageView>(R.id.new_event_vh_tag_image)
        val eventTags = currentEvents.eventsModel.eventTags
        if (eventTags.size > 1){
            val eventTag = eventTags[0]
            when(eventTag){
                "Tech", "MeetUps" -> {
                    //TODO: FIND BETTER ICON!
                    eventTagImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_robot))
                }
                "School", "Tests", "Valedictory Service", "Convocations", "Examination", "Send-Offs", "Skills and Acquisitions"-> {
                    eventTagImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_school))
                }
                "Movements" -> {
                    //TODO: FIND BETTER ICON!
                    eventTagImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_basketball))
                }
                "Christian" -> {
                    eventTagImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_church))
                }
                "Sports", "Football" -> {
                    eventTagImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_soccer))
                }
                "Basketball" -> {
                    eventTagImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_basketball))
                }
                "Athletics" -> {
                    //TODO: FIND BETTER ICON!
                    eventTagImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_basketball))

                }
                "Muslim" -> {
                    eventTagImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_mosque))

                }
                "Parties", "Galas" -> {
                    eventTagImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_basketball))
                    //TODO: FIND BETTER ICON!
                }

                "Business" -> {
                    //TODO: FIND BETTER ICON!
                    eventTagImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_basketball))

                }

                "Birthday" -> {
                    eventTagImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_birthday_cake))
                }

                "Conventions/Conferences", "Workshops/Seminars", "Motivation" -> {
                    eventTagImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_stage))

                }
                "Relationship" -> {
                    //TODO: FIND BETTER ICON!
                    eventTagImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_basketball))

                }
            }
        }


//        val goingCountTV = holder.itemView.findViewById<TextView>(R.id.explore_viewholder_going_count)
        val commentCountTV = holder.itemView.findViewById<TextView>(R.id.new_event_vh_comment_count)
        val commentBtn = holder.itemView.findViewById<ImageView>(R.id.new_event_vh_comment_button)

        fireStoreRepo.getDocumentCommentCollection(currentEvents.eventID).addSnapshotListener { snapshot, _ ->
            if (snapshot!= null && !snapshot.isEmpty  ){
                commentCountTV.text = snapshot.size().toString()
            }else{
                commentCountTV.text = "0"
            }
        }

        commentBtn.setOnClickListener {
            val intent = Intent(context, CommentActivity::class.java)
            intent.putExtra("eventID", currentEvents.eventID)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle())
            }else{
                context.startActivity(intent)
            }
        }

//        fireStoreRepo.getDocumentLikesCollection(currentEvents.eventID)
//            .addSnapshotListener { snapshot, _ ->
//
//                if (snapshot!= null && !snapshot.isEmpty  ){
//                    goingCountTV.text = snapshot.size().toString()
//                }else{
//                    goingCountTV.text = "0"
//                }
//
//            }


//        val button = holder.itemView.findViewById<ImageButton>(R.id.explore_viewholder_going_btn)
//        fireStoreRepo.getDocumentLikesCollection(currentEvents.eventID).document(FirebaseUtils().getUserUID(context))
//            .addSnapshotListener { snapshot, _ ->
//                if (snapshot != null && snapshot.exists()){
//                    button.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_going_two))
//                }else{
//                    button.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_going))
//                }
//            }
//
//
//        button.setOnClickListener {
//            fireStoreRepo.likeDislikeEvent(context, currentEvents.eventID, it)
//        }

        val postTime = currentEvents.eventsModel.eventPostTime

        if (postTime!= null && postTime != "null"){

            val longTimeValue = postTime.toLong()
            val finalDate = DateFormatterUtil.getRelativeTimeSpanStringShort(context, longTimeValue).toString()

            holder.itemView.findViewById<TextView>(R.id.new_event_vh_post_date).text =finalDate


        }else{
            val finalDate =  "Getting date.."
            holder.itemView.findViewById<TextView>(R.id.new_event_vh_post_date).text =finalDate
        }

    }

}