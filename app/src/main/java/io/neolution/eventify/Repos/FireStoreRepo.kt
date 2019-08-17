package io.neolution.eventify.Repos

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import id.zelory.compressor.Compressor
import io.neolution.eventify.Data.ModelClasses.EventsModel
import io.neolution.eventify.Data.ModelClasses.toHashMap
import io.neolution.eventify.R
import io.neolution.eventify.Utils.FirebaseUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException


/**
 * Created by Big-Nosed Developer on the Edge of Infinity.
 */
class FireStoreRepo {

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private fun getFireStoreInstance(): FirebaseFirestore = db

    companion object {

        @JvmStatic
        fun getFireStoreRepoInstance() : FireStoreRepo{
            return FireStoreRepo()
        }

    }

    fun getDocumentUpdatesPath(documentID: String): CollectionReference{
        return getEventDocumentRefererenceById(documentID).collection(FirebaseUtils.updatesCollectionPath)
    }

    fun getCurrentUserDocumentPath(): DocumentReference {
        return getFireStoreInstance().collection(FirebaseUtils.userCollectionPath)
            .document(AuthRepo.getUserUid())

    }

    fun getUsersDocumentPath(): CollectionReference {
        return getFireStoreInstance().collection(FirebaseUtils.userCollectionPath)
    }

    fun getUserAccountDetailsFromUid(uid: String): DocumentReference {
//        val hashMap = HashMap<String, Any>()
        return getUsersDocumentPath().document(uid)
    }

    fun createUserAccount(
        userName: String, userEmail: String, bio: String, interestedTags: List<String>?,
        body: () -> Unit, ifNotSuccessful: () -> Unit, userPic: String?, userImageThumb: String?
    ) {
        val userCollectionRef = db.collection(FirebaseUtils.userCollectionPath).document(AuthRepo.getUserUid())


        val map = HashMap<String, Any?>()
        map["userName"] = userName
        map["userEmail"] = userEmail
        map["userImage"] = userPic
        map["userPicThumbLink"] = userImageThumb
        map["userBio"] = bio
        map["tags"] = interestedTags


        userCollectionRef.set(map).addOnCompleteListener {
            if (it.isSuccessful) {
                body()
            } else {
                ifNotSuccessful()
            }
        }

    }

    fun updateInterestedTags(
        interestedTags: List<String>,
        body: () -> Unit,
        ifNotSuccessful: (exception: String) -> Unit
    ) {
        val userCollectionRef = getFireStoreInstance().collection(FirebaseUtils.userCollectionPath)
            .document(AuthRepo.getUserUid())
        userCollectionRef.update("tags", interestedTags).addOnCompleteListener {
            if (it.isSuccessful) {
                body()
            } else {
                ifNotSuccessful(it.exception.toString())
            }
        }
    }

    fun likeDislikeEvent(context: Context, eventID: String, view: View){

        val button = view as ImageView

        getEventDocumentRefererenceById(eventID).collection("Likes")
            .document(AuthRepo.getUserUid()).get().addOnSuccessListener {
                if (it != null && it.exists()) {

                    getEventDocumentRefererenceById(eventID).collection("Likes")
                        .document(AuthRepo.getUserUid()).delete().addOnSuccessListener {
                            button.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pin_unpinned))
                            Toast.makeText(context, "You will no more get updates about this event!", Toast.LENGTH_LONG)
                                .show()
                        }

                } else {

                    val timeStamp = FieldValue.serverTimestamp()
                    val hashMap = HashMap<String, Any>()
                    hashMap["timestamp"] = timeStamp

                    getEventDocumentRefererenceById(eventID).collection("Likes")
                        .document(AuthRepo.getUserUid()).set(hashMap).addOnSuccessListener {
                            button.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pin_pinned))
                            Toast.makeText(context, "You will now get updates about this event!", Toast.LENGTH_LONG)
                                .show()
                        }

                }
            }

    }


    fun postEvent(context: Context, eventsModel: EventsModel, ifCompleted: () -> Unit, ifNotCompeted: (exception: String) -> Unit) {

        val imageUri = Uri.parse(eventsModel.eventImageLink)
        val compressedImage = compressImageUri(context, imageUri)

        var finalImageLink: String? =  null
        var finalImageThumbLink: String? = null

        val map = eventsModel.toHashMap()

        val particularOperation = db.collection(FirebaseUtils.eventsCollectionPath)

        particularOperation.add(map).addOnCompleteListener { initialMapTask ->
            if (initialMapTask.isSuccessful) {

                val eventID = initialMapTask.result!!.id
                FirebaseStorageRepo.putEventPoster(eventID, imageUri).addOnCompleteListener { eventPosterTask ->
                    if (eventPosterTask.isSuccessful) {
                        FirebaseStorageRepo.putEventThumb(eventID, compressedImage).addOnCompleteListener { eventThumbTask ->
                            if (eventThumbTask.isSuccessful) {
                                FirebaseStorageRepo.getEventPosterUrl(eventID).addOnSuccessListener {

                                    finalImageLink = it.toString()

                                    FirebaseStorageRepo.getEventThumbUri(eventID).addOnSuccessListener { thmbUrl ->

                                        finalImageThumbLink = thmbUrl.toString()

                                        particularOperation.document(initialMapTask.result!!.id).update(
                                            "eventImageLinkThumb", finalImageThumbLink, "eventImageLink", finalImageLink
                                        ).addOnCompleteListener {
                                            if (eventPosterTask.isSuccessful){
                                                ifCompleted()
                                            }else{
                                                ifNotCompeted(eventPosterTask.exception!!.localizedMessage)
                                            }
                                        }
                                    }
                                }

                            }else{
                                ifNotCompeted(eventThumbTask.exception!!.localizedMessage)
                            }

                        }

                    }else{
                        ifNotCompeted(eventPosterTask.exception!!.localizedMessage)
                    }
                }



            } else {
                ifNotCompeted(initialMapTask.exception!!.localizedMessage)
            }
        }

    }

    fun postPromotedEvent(context: Context, eventsModel: EventsModel, ifCompleted: () -> Unit, ifNotCompeted: (exception: String) -> Unit) {

        val imageUri = Uri.parse(eventsModel.eventImageLink)
        val compressedImage = compressImageUri(context, imageUri)

        var finalImageLink: String? =  null
        var finalImageThumbLink: String? = null

        val map = eventsModel.toHashMap()

        val particularOperation = db.collection(FirebaseUtils.promotedCollectionPath)

        particularOperation.add(map).addOnCompleteListener { initialMapTask ->
            if (initialMapTask.isSuccessful) {

                val eventID = initialMapTask.result!!.id
                FirebaseStorageRepo.putEventPoster(eventID, imageUri).addOnCompleteListener { eventPosterTask ->
                    if (eventPosterTask.isSuccessful) {
                        FirebaseStorageRepo.putEventThumb(eventID, compressedImage).addOnCompleteListener { eventThumbTask ->
                            if (eventThumbTask.isSuccessful) {
                                FirebaseStorageRepo.getEventPosterUrl(eventID).addOnSuccessListener {
                                    finalImageLink = it.toString()

                                    FirebaseStorageRepo.getEventThumbUri(eventID).addOnSuccessListener { thmbUrl ->

                                        finalImageThumbLink = thmbUrl.toString()

                                        particularOperation.document(initialMapTask.result!!.id).update(
                                            "eventImageLinkThumb", finalImageThumbLink, "eventImageLink", finalImageLink
                                        ).addOnCompleteListener {
                                            if (eventPosterTask.isSuccessful){

                                                ifCompleted()
                                            }else{
                                                ifNotCompeted(eventPosterTask.exception!!.localizedMessage)
                                            }
                                        }
                                    }
                                }

                            }else{
                                ifNotCompeted(eventThumbTask.exception!!.localizedMessage)
                            }

                        }

                    }else{
                        ifNotCompeted(eventPosterTask.exception!!.localizedMessage)
                    }
                }



            } else {
                ifNotCompeted(initialMapTask.exception!!.localizedMessage)
            }
        }

    }

    fun getEventPosts(): CollectionReference{
        return getFireStoreInstance().collection(FirebaseUtils.eventsCollectionPath)
    }

    fun getPromotedEvents(): CollectionReference{
        return getFireStoreInstance().collection(FirebaseUtils.promotedCollectionPath)
    }

    fun getPromotedEventClickedCollection(eventID: String): CollectionReference{
        return getPromotedEvents().document(eventID).collection(FirebaseUtils.clickedCollectionPath)
    }


    fun updateUserAccount( userName: String, userBio: String, userPicLink: String?, ifCompleted: () -> Unit, ifNotCompeted: () -> Unit) {
        val documentReference = getFireStoreInstance().collection(FirebaseUtils.userCollectionPath).document(AuthRepo.getUserUid())

        if (userPicLink != null){
            val uri = Uri.parse(userPicLink)
            var finalPicLink : String?

            FirebaseStorageRepo.putUserImage(uri).addOnCompleteListener { picLinkTask ->
                if (picLinkTask.isSuccessful){
                    FirebaseStorageRepo.getUserImage().addOnSuccessListener {
                        finalPicLink = it.toString()

                        val task = documentReference.update("userName" , userName,
                            "userBio", userBio,
                            "userImage", finalPicLink)
                        task.addOnCompleteListener {
                            if(task.isSuccessful){
                                ifCompleted()
                            }else{
                                ifNotCompeted()
                            }
                        }

                    }
                }
            }
        }else{

            val task = documentReference.update("userName" , userName,
                "userBio", userBio)
            task.addOnCompleteListener {
                if(task.isSuccessful){
                    ifCompleted()
                }else{
                    ifNotCompeted()
                }
            }

        }



    }

    fun getEventDocumentRefererenceById(postID: String): DocumentReference{
        return getEventPosts().document(postID)
    }

    private fun compressImageUri(context: Context, imageUri: Uri): ByteArray{

        var thumbBitmap : Bitmap? = null

        //Compressing Image
        val thumbPath = File(imageUri.path)

        try {
            thumbBitmap = Compressor(context)
                .setMaxWidth(200)
                .setMaxHeight(200)
                .setQuality(10)
                .compressToBitmap(thumbPath)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val baos = ByteArrayOutputStream()
        thumbBitmap?.compress(Bitmap.CompressFormat.JPEG, 10, baos)
        return baos.toByteArray()
    }

    fun getDocumentLikesCollection(eventID: String) = getEventDocumentRefererenceById(eventID).collection("Likes")

    fun getDocumentCommentCollection(eventID: String) = getEventDocumentRefererenceById(eventID).collection("Comment")

}