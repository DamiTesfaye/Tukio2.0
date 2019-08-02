package io.neolution.eventify.Data.LiveDatas

import androidx.lifecycle.LiveData
import io.neolution.eventify.Data.ModelClasses.UserModel
import io.neolution.eventify.Repos.FireStoreRepo

/**
 * Created by Big-Nosed Developer on the Edge of Infinity.
 */
class AccountLiveData: LiveData<UserModel>() {

    private val repo = FireStoreRepo()

    override fun onActive() {
        super.onActive()

        repo.getCurrentUserDocumentPath().get().addOnSuccessListener {

            if(it != null){
                value = it.toObject(UserModel::class.java)
            }

        }
    }

    override fun onInactive() {
        super.onInactive()


    }
}