package com.exceptos.tukio.Data.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FieldValue
import com.exceptos.tukio.Data.ModelClasses.FullEventsModel
import com.exceptos.tukio.Data.ModelClasses.breakDownToUserModel
import com.exceptos.tukio.Data.ModelClasses.passIntoIntent
import com.exceptos.tukio.Listeners.OnAddReminderClicked
import com.exceptos.tukio.Listeners.OnShareEventClicked
import com.exceptos.tukio.R
import com.exceptos.tukio.Repos.FireStoreRepo
import com.exceptos.tukio.Utils.AppUtils
import com.exceptos.tukio.Utils.DateFormatterUtil
import com.exceptos.tukio.Utils.FirebaseUtils
import com.exceptos.tukio.View.Activities.CommentActivity
import com.exceptos.tukio.View.Activities.EventDetailsActivity
import com.exceptos.tukio.View.Activities.UpdatesActivity

/**
 * Created by Big-Nosed Developer on the Edge of Infinity.
 */
class HomeAdapter(val context: Context,
                  val listOfEvents: List<FullEventsModel>,
                  val activity: FragmentActivity, var shareEventListener: OnShareEventClicked, val onAddReminderClicked: OnAddReminderClicked)
    : RecyclerView.Adapter<com.exceptos.tukio.Data.Adapters.HomeAdapter.HomeViewholder>() {

    private  var  fireStoreRepo: FireStoreRepo

    init {
        fireStoreRepo = FireStoreRepo()
    }

    class HomeViewholder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): com.exceptos.tukio.Data.Adapters.HomeAdapter.HomeViewholder {
        val view = LayoutInflater.from(context).inflate(R.layout.new_event_viewholder, parent, false)
        return com.exceptos.tukio.Data.Adapters.HomeAdapter.HomeViewholder(view)
    }

    override fun getItemCount() = listOfEvents.size

    override fun onBindViewHolder(holder: com.exceptos.tukio.Data.Adapters.HomeAdapter.HomeViewholder, position: Int) {

        holder.setIsRecyclable(false)

        val currentEvents = listOfEvents[position]
        val userInfo = fireStoreRepo.getUserAccountDetailsFromUid(currentEvents.eventsModel.userUID)

        var userName = ""

        userInfo.get().addOnSuccessListener { snapshot ->
            if (snapshot != null && snapshot.exists()){
                val userModel = snapshot.breakDownToUserModel()

                userName = userModel.userName
                val userPicLink = userModel.userImage
                holder.itemView.findViewById<TextView>(R.id.new_event_vh_username).background = ContextCompat.getDrawable(context, R.drawable.transparent_drawable)

                if (userName.length <= 30){
                    holder.itemView.findViewById<TextView>(R.id.new_event_vh_username).text = userName
                }else{
                    val shortenedUsername = userName.substring(0, 30) + "..."
                    holder.itemView.findViewById<TextView>(R.id.new_event_vh_username).text = shortenedUsername
                }

                val imageView = holder.itemView.findViewById<ImageView>(R.id.new_event_vh_user_image)

                val requestOptions = RequestOptions()
                requestOptions.placeholder(ContextCompat.getDrawable(context, R.drawable.ic_male_placeholder))
                val thumbNailRequest = Glide.with(context.applicationContext).load(userModel.userThumbLink)

                Glide.with(context.applicationContext)
                    .setDefaultRequestOptions(requestOptions)
                    .asDrawable()
                    .load(userPicLink)
                    .thumbnail(thumbNailRequest)
                    .into(imageView)

            }
        }.addOnFailureListener {

        }

        val promotedTag = holder.itemView.findViewById<TextView>(R.id.new_event_vh_promoted_tag)
        if (currentEvents.eventsModel.eventType == "promoted"){
            promotedTag.visibility = VISIBLE
        }else{
            promotedTag.visibility = GONE
        }

        if (currentEvents.eventsModel.eventLocation.length <= 80){
            holder.itemView.findViewById<TextView>(R.id.new_event_vh_location_text)
                .text = currentEvents.eventsModel.eventLocation
        }else{
            val shortenedLocation = currentEvents.eventsModel.eventLocation.substring(0, 80) + "..."
            holder.itemView.findViewById<TextView>(R.id.new_event_vh_location_text)
                .text = shortenedLocation
        }

        val reminderButton = holder.itemView.findViewById<ImageButton>(R.id.new_event_vh_reminder_btn)
        reminderButton.visibility = GONE

        holder.itemView.findViewById<ImageView>(R.id.new_event_vh_share_event).setOnClickListener {

            shareEventListener.onShareButtonClick(eventTitle = currentEvents.eventsModel.eventTitle,
                eventLocation = currentEvents.eventsModel.eventLocation, eventDate = currentEvents.eventsModel.eventDate,
                eventId = currentEvents.eventID)
        }

        val imageView = holder.itemView.findViewById<ImageView>(R.id.new_event_vh_event_image)
        val requestOptions = RequestOptions()
        requestOptions.placeholder(ContextCompat.getDrawable(context, R.drawable.placeholder_2))

        val thumbNailRequest = Glide.with(context).load(currentEvents.eventsModel.eventImageLinkThumb)
        Glide.with(context.applicationContext)
            .setDefaultRequestOptions(requestOptions)
            .asDrawable()
            .thumbnail(thumbNailRequest)
            .load(currentEvents.eventsModel.eventImageLink)
            .into(imageView)

        holder.itemView.setOnClickListener {

            //TODO: MOVE THIS OFF THE MAIN THREAD
            if ( userName != "" ){

                currentEvents.eventsModel.eventType?.let {eventType ->
                    if (eventType == "promoted"){

                        if (currentEvents.eventsModel.userUID != FirebaseUtils().getUserUID(context)){
                            fireStoreRepo.getPromotedEventClickedCollection(currentEvents.eventID).document(FirebaseUtils().getUserUID(context))
                                .get().addOnSuccessListener { document ->
                                    if (document != null && document.exists()){

                                    }else{

                                        val timeStamp = FieldValue.serverTimestamp()
                                        val hashMap = HashMap<String, Any>()
                                        hashMap["timestamp"] = timeStamp

                                        fireStoreRepo.getPromotedEventClickedCollection(currentEvents.eventID).document(FirebaseUtils().getUserUID(context)).set(hashMap)
                                    }

                                }
                        }


                    }
                }

                val intent = listOfEvents[position].passIntoIntent(context, EventDetailsActivity::class.java, eventLocation = currentEvents.eventsModel.eventLocation
                    , userName = userName, startedFrom = this.javaClass.name)
                context.startActivity(intent)
            }
        }

        holder.itemView.findViewById<TextView>(R.id.new_event_vh_event_title_text).text = currentEvents.eventsModel.eventTitle
        holder.itemView.findViewById<TextView>(R.id.new_event_vh_date_text).text = currentEvents.eventsModel.eventDate

        val updateImageButton = holder.itemView.findViewById<ImageView>(R.id.new_event_vh_event_updates)

        fireStoreRepo.getDocumentUpdatesPath(currentEvents.eventID).get().addOnSuccessListener { snapshot ->
            if (snapshot != null && !snapshot.isEmpty){
                updateImageButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_notification_added))
            }else{
                updateImageButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_notification))
            }
        }

        updateImageButton.setOnClickListener {

            val intent = Intent(context, UpdatesActivity::class.java)
            intent.putExtra("documentID", currentEvents.eventID)
            intent.putExtra("posterID",currentEvents.eventsModel.userUID)
            context.startActivity(intent)

        }

        val eventTagImage = holder.itemView.findViewById<ImageView>(R.id.new_event_vh_tag_image)
        val eventTags = currentEvents.eventsModel.eventTags

        val eventTag = eventTags[0]
        val chipTags = AppUtils.createChipList()

        for (chipTag in chipTags){
            if (chipTag.chipText == eventTag){
                eventTagImage.setImageDrawable(ContextCompat.getDrawable(context, chipTag.imageResource))
            }
        }

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
            context.startActivity(intent)
        }


        val willAttendButton = holder.itemView.findViewById<ImageView>(R.id.new_event_vh_attend_button)
        val attendanceCount = holder.itemView.findViewById<TextView>(R.id.new_event_vh_attend_count)
        fireStoreRepo.getDocumentLikesCollection(currentEvents.eventID)
            .addSnapshotListener { snapshot, _ ->

                if (snapshot!= null && !snapshot.isEmpty  ){
                    attendanceCount.text = snapshot.size().toString()
                }else{
                    attendanceCount.text = "0"
                }

            }

        fireStoreRepo.getDocumentLikesCollection(currentEvents.eventID).document(FirebaseUtils().getUserUID(context)).get().addOnSuccessListener { snapshot ->
            if (snapshot != null && snapshot.exists()){
                willAttendButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pin_pinned))
            }else{
                willAttendButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pin_unpinned))
            }
        }.addOnFailureListener {

        }

        willAttendButton.setOnClickListener {
            fireStoreRepo.likeDislikeEvent(context, currentEvents.eventID, it)
        }


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