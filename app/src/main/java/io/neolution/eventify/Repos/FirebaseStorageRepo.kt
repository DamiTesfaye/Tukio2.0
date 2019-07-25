package io.neolution.eventify.Repos

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

/**
 * Created by Big-Nosed Developer on the Edge of Infinity.
 */
class FirebaseStorageRepo {

    companion object {

        private const val IMAGE_PATH = "user_image"
        private const val THUMBNAIL_PATH = "thumb_nail"
        private const val EVENT_POSTER_PATH = "event_poster"
        private const val EVENT_POSTER_THUMB_PATH = "event_poster_thumb"

        private fun getFirebaseStorageInstancePath(path: String): StorageReference{
           return FirebaseStorage.getInstance().getReference(path)
        }

        fun putUserImage(uri: Uri): UploadTask{
            return getFirebaseStorageInstancePath(IMAGE_PATH).child("${AuthRepo.getUserUid()}.jpg").putFile(uri)
        }

        fun putUserImageThumb(uri: Uri): UploadTask{
            return getFirebaseStorageInstancePath(THUMBNAIL_PATH).child("${AuthRepo.getUserUid()}.jpg").putFile(uri)
        }

        fun getUserImage():Task<Uri>{
            return getFirebaseStorageInstancePath("$IMAGE_PATH/${AuthRepo.getUserUid()}.jpg").downloadUrl
        }

        fun getUserImageThumb(): Task<Uri>{
            return getFirebaseStorageInstancePath("$THUMBNAIL_PATH/${AuthRepo.getUserUid()}.jpg").downloadUrl
        }

        fun putEventPoster(eventID: String, uri: Uri): UploadTask {
            return getFirebaseStorageInstancePath(EVENT_POSTER_PATH).child("$eventID.jpg").putFile(uri)
        }

        fun putEventThumb(eventID: String, compressedImage: ByteArray): UploadTask{
            return getFirebaseStorageInstancePath(EVENT_POSTER_THUMB_PATH).child("$eventID.jpg").putBytes(compressedImage)
        }

        fun getEventPosterUrl(eventID: String) : Task<Uri> {
            return getFirebaseStorageInstancePath("$EVENT_POSTER_PATH/$eventID.jpg").downloadUrl
        }

        fun getEventThumbUri(eventID: String): Task<Uri> {
            return getFirebaseStorageInstancePath("$EVENT_POSTER_PATH/$eventID.jpg").downloadUrl
        }

    }



}