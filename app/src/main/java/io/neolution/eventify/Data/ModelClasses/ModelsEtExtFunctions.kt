package io.neolution.eventify.Data.ModelClasses

import android.content.Context
import android.content.Intent
import android.support.annotation.DrawableRes
import android.support.v4.graphics.TypefaceCompat
import android.support.v7.widget.RecyclerView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.DocumentSnapshot
import io.neolution.eventify.Utils.AppUtils

/**
 * Created by Big-Nosed Developer on the Edge of Infinity.
 */
data class EventsModel(var eventType: String?,
                       var eventTitle: String,
                       var eventDesc: String,
                       var eventLocation: String,
                       var eventDate: String,
                       var eventTags: List<String>,
                       var eventRegLink: String?,
                       var userUID: String,
                       var eventTicketLink: String?,
                       var eventDressCode: String?,
                       var eventImageLink: String,
                       var eventGuests: HashMap<String, Any>?,
                       var eventImageLinkThumb: String?,
                       var eventPostTime: String?,
                       var amountPaid: Long?,
                       var eventMilis: Long?
)  {
    constructor() : this("", "", "", "", "", arrayListOf<String>(),
        "", "", "", "", "" , HashMap<String, Any>(), "", "", 0, 0)

}

data class ChipModel(var chipText: String, @DrawableRes var imageResource: Int)

data class NewUpdateModel(var updateType: String, var updateValue: Any)

data class FullEventsModel(var eventsModel: EventsModel,
                       var eventID: String
)  {
    constructor() : this(EventsModel("","", "", "", "", arrayListOf<String>(),
        "", "", "", "", ""
        , HashMap<String, Any>(), "", "", 0, 0), "")

}

data class UpdatesModel(var updateTitle: String, var updateDesc: String){
    constructor(): this ("", "")
}

data class FullUpdateModel(var updatesModel: UpdatesModel, var eventName: String?){
    constructor(): this (UpdatesModel("", ""), "")
}

data class GoingModel(var userUID: String){

    constructor(): this("")

}

data class FullGoingModel(var goingModel: GoingModel, var goingDocument: String){
    constructor(): this(GoingModel(""), "")
}

data class CommentModel(val posterUID: String, var commentMessage: String, val currentTimePosted: Long){
    constructor(): this("", "", 0L)
}

data class GuestModel(var guestName: String, var guestBio: String)

data class UserModel(var userName: String,
                     var userEmail: String,
                     var userImage: String?,
                     var userThumbLink: String?,
                     var userBio: String,
                     var tags: List<String>?){

    constructor() : this("", "", "", "",  "", arrayListOf<String>())

}


fun UpdatesModel.toHashMap(): HashMap<String, Any>{

    val hashMap = HashMap<String, Any>()

    val updateTitle = this.updateTitle
    val updateDesc = this.updateDesc

    hashMap["updateTitle"] = updateTitle
    hashMap["updateDesc"] = updateDesc

    return hashMap
}

fun NewUpdateModel.toHashMap(): HashMap<String, Any>{
    val hashMap = HashMap<String, Any>()

    val updateType = this.updateType
    val updateValue = this.updateValue

    hashMap["updateType"] = this.updateType
    hashMap["updateValue"] = this.updateValue

    return hashMap

}

fun CommentModel.toHashMap(): HashMap<String, Any>{
    val hashMap = HashMap<String, Any>()

    hashMap["commentMessage"] = this.commentMessage
    hashMap["posterUID"] = this.posterUID
    hashMap["currentTimePosted"] = this.currentTimePosted

    return hashMap
}

fun DocumentSnapshot.breakDownToCommentModel(): CommentModel{
    val posterUID = this["posterUID"].toString()
    val commentMessage = this["commentMessage"].toString()
    val currentTimePosted = this["currentTimePosted"] as Long

    return CommentModel(posterUID = posterUID, commentMessage = commentMessage, currentTimePosted = currentTimePosted)
}

fun DocumentSnapshot.breakDownToUpdatesModel(): UpdatesModel{
    val updateTitle = this["updateTitle"].toString()
    val updateDesc =  this["updateDesc"].toString()

    return UpdatesModel(updateTitle, updateDesc)
}

fun DocumentSnapshot.breakDocumentIntoEvntsModel(): EventsModel {
    val eventType = this["eventType"].toString()
    val eventTitle = this["eventTitle"].toString()
    val eventDesc = this["eventDesc"].toString()
    val eventLocation = this["eventLocation"].toString()
    val eventDate = this["eventDate"].toString()
    val eventTags = this["eventTags"] as List<String>
    val eventRegLink = this["eventRegLink"].toString()
    val userUID = this["userUID"].toString()
    val eventTicketLink = this["eventTicketLink"].toString()
    val eventDressCode = this["eventDressCode"].toString()
    val eventImageLink = this["eventImageLink"].toString()
    val listOfGuests = this["eventGuests"] as HashMap<String, Any>?
    val eventImageLinkThumb = this["eventImageLinkThumb"].toString()
    val eventPostTime = this["eventPostTime"].toString()
    val amountPaid = this["amountPaid"] as Long?
    val eventMilis = this["eventMilis"] as Long?

    return EventsModel(eventType, eventTitle, eventDesc, eventLocation,
        eventDate, eventTags, eventRegLink, userUID, eventTicketLink, eventDressCode, eventImageLink,
        listOfGuests, eventImageLinkThumb, eventPostTime, amountPaid, eventMilis)
}

fun DocumentSnapshot.breakDownToUserModel(): UserModel{
    val userName = this["userName"].toString()
    val userEmail = this["userEmail"].toString()
    val userPicLink = this["userImage"].toString()
    val userThumbLink = this["userThumbLink"].toString()
    val userBio = this["userBio"].toString()
    val tags = this["tags"] as List<String>?

    return UserModel(userName, userEmail, userPicLink,userThumbLink, userBio, tags)
}

fun RecyclerView.initRecyclerView(layoutManagerType: String, context: Context){
    if (layoutManagerType == AppUtils.STAGGERED_LAYOUT_MANAGER){
        this.layoutManager = AppUtils.getRecycleLayout(AppUtils.STAGGERED_LAYOUT_MANAGER, context)
        this.setHasFixedSize(true)
    }else if (layoutManagerType == AppUtils.LINEAR_LAYOUT_MANAGER){
        this.layoutManager = AppUtils.getRecycleLayout(AppUtils.LINEAR_LAYOUT_MANAGER, context)
        this.setHasFixedSize(true)
    }
}

fun MutableList<FullEventsModel>.compareAccordingToMoneyPaid(): MutableList<FullEventsModel>{
    val firstModel = this[0]
    val secondModel = this[1]

    if (firstModel.eventsModel.amountPaid!! < secondModel.eventsModel.amountPaid!!){

        val list = mutableListOf<FullEventsModel>()
        list.add(0, secondModel)
        list.add(1, firstModel)

        return list
    }else{
        return this
    }
}

fun MutableList<FullEventsModel>.addPromotedEventsIntoNormalEvents(promotedEventsList: List<FullEventsModel>){

    if(promotedEventsList.size > 1 && size > 4){
        val indexOfFirstPromotedEvent  = 1
        val indexOfSecondPromotedEvent = 5

        if(!contains(promotedEventsList[0]) && !contains(promotedEventsList[1])) {
            add(indexOfFirstPromotedEvent, promotedEventsList[0])
            add(indexOfSecondPromotedEvent, promotedEventsList[1])
        }

    }else{
        val indexOfFirstPromotedEvent  = 1
        if (!contains(promotedEventsList[0])){
            add(indexOfFirstPromotedEvent, promotedEventsList[0])
        }

    }

}

fun List<String>.compareLists(otherList: List<String>): Int{
    var sameItems = 0

    for (value in this){
        if (otherList.contains(value)){
            sameItems++
        }
    }

    return sameItems

}

 fun FullEventsModel.passIntoIntent(context: Context, activity: Class<*>, startedFrom: String,
                                    userName: String, eventLocation: String): Intent{
    val intent = Intent(context, activity)

    intent.putExtra("documentID", this.eventID)
     intent.putExtra("posterUID", this.eventsModel.userUID)
    intent.putExtra("startedFrom", startedFrom)
    intent.putExtra("eventTitle", this.eventsModel.eventTitle)
    intent.putExtra("eventDesc", this.eventsModel.eventDesc)
    intent.putExtra("eventLocation", eventLocation)
    intent.putExtra("eventTicketLink", this.eventsModel.eventTicketLink)
    intent.putExtra("eventRegLink", this.eventsModel.eventRegLink)
    intent.putExtra("eventTicketLink", this.eventsModel.eventTicketLink)
    intent.putExtra("eventDate", this.eventsModel.eventDate)
    intent.putExtra("eventDressCode", this.eventsModel.eventDressCode)
    intent.putExtra("eventImageLink", this.eventsModel.eventImageLink)
     intent.putExtra("eventType", this.eventsModel.eventType)
    intent.putExtra("userName", userName)

    return intent

}

fun EventsModel.toHashMap(): HashMap<String, Any?>{
   val hashMap = HashMap<String, Any?>()

    hashMap["eventType"] = this.eventType
    hashMap["eventTitle"] = this.eventTitle
    hashMap["eventDesc"] = this.eventDesc
    hashMap["eventLocation"] = this.eventLocation
    hashMap["eventDate"] = this.eventDate
    hashMap["userUID"] = this.userUID
    hashMap["eventTags"] = this.eventTags
    hashMap["eventRegLink"] = this.eventRegLink
    hashMap["eventTicketLink"] = this.eventTicketLink
    hashMap["eventDressCode"] = this.eventDressCode
    hashMap["eventImageLink"] = this.eventImageLink
    hashMap["eventGuests"] = this.eventGuests
    hashMap["eventImageLinkThumb"] = this.eventImageLinkThumb
    hashMap["eventPostTime"] = this.eventPostTime
    hashMap["amountPaid"] = this.amountPaid
    hashMap["eventMilis"] = this.eventMilis

    return hashMap
}

fun Context.indicate(message: String){
    Toast.makeText(this, message, Toast.LENGTH_SHORT)
        .show()
}
