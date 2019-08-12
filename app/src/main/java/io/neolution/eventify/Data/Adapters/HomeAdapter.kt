package io.neolution.eventify.Data.Adapters

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
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
import de.hdodenhof.circleimageview.CircleImageView
import io.neolution.eventify.Data.ModelClasses.FullEventsModel
import io.neolution.eventify.Data.ModelClasses.breakDownToUserModel
import io.neolution.eventify.Data.ModelClasses.passIntoIntent
import io.neolution.eventify.Listeners.OnAddReminderClicked
import io.neolution.eventify.Listeners.OnShareEventClicked
import io.neolution.eventify.R
import io.neolution.eventify.Repos.FireStoreRepo
import io.neolution.eventify.Utils.AppUtils
import io.neolution.eventify.Utils.DateFormatterUtil
import io.neolution.eventify.Utils.FirebaseUtils
import io.neolution.eventify.View.Activities.CommentActivity
import io.neolution.eventify.View.Activities.EventDetailsActivity
import io.neolution.eventify.View.Activities.FullDetails
import io.neolution.eventify.View.Activities.UpdatesActivity

/**
 * Created by Big-Nosed Developer on the Edge of Infinity.
 */
class HomeAdapter(val context: Context,
                  val listOfEvents: List<FullEventsModel>,
                  val activity: FragmentActivity, var shareEventListener: OnShareEventClicked,
                  var onAddReminderClicked: OnAddReminderClicked)
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

        userInfo.addSnapshotListener { snapshot, _ ->
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

                val imageView = holder.itemView.findViewById<CircleImageView>(R.id.new_event_vh_user_image)

                val requestOptions = RequestOptions()
                requestOptions.placeholder(ContextCompat.getDrawable(context, R.drawable.ic_default_user))
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

        if (currentEvents.eventsModel.eventLocation.length <= 80){
            holder.itemView.findViewById<TextView>(R.id.new_event_vh_location_text)
                .text = currentEvents.eventsModel.eventLocation
        }else{
            val shortenedLocation = currentEvents.eventsModel.eventLocation.substring(0, 80) + "..."
            holder.itemView.findViewById<TextView>(R.id.new_event_vh_location_text)
                .text = shortenedLocation
        }

        val reminderButton = holder.itemView.findViewById<ImageButton>(R.id.new_event_vh_reminder_btn)
        if (currentEvents.eventsModel.eventMilis == null){
            reminderButton.visibility = GONE
        }else{
            reminderButton.visibility = VISIBLE
            reminderButton.setOnClickListener {
                onAddReminderClicked.OnAddReminderButtonClicked(currentEvents.eventsModel.eventMilis!!)
            }
        }

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

                val intent = Intent(context, EventDetailsActivity::class.java)
                context.startActivity(intent)

//                val intent = listOfEvents[position].passIntoIntent(context, FullDetails::class.java, eventLocation = currentEvents.eventsModel.eventLocation
//                    , userName = userName, startedFrom = this.javaClass.name)
//                context.startActivity(intent)
            }
        }

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