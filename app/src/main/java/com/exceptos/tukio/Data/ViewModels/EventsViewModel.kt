package com.exceptos.tukio.Data.ViewModels

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.CollectionReference
import com.exceptos.tukio.Repos.FireStoreRepo

/**
 * Created by Big-Nosed Developer on the Edge of Infinity.
 */
class EventsViewModel: ViewModel() {

    val repo = FireStoreRepo()


    fun getEventDocuments(): CollectionReference{
        return repo.getEventPosts()
    }

    fun getPromotedEvents(): CollectionReference{
        return repo.getPromotedEvents()
    }

}