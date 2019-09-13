package com.exceptos.tukio.Data.ViewModels

import androidx.lifecycle.ViewModel
import com.exceptos.tukio.Repos.FireStoreRepo

/**
 * Created by Big-Nosed Developer on the Edge of Infinity.
 */
class ProfileViewModel: ViewModel() {

    val fireStoreRepo = FireStoreRepo()

    fun getAccountInfo() = com.exceptos.tukio.Data.LiveDatas.AccountLiveData()

    fun updateInterestedTags(interestedTags: List<String>, ifNotSuccessful: (exception: String) -> Unit, if_Successful: () -> Unit){
        fireStoreRepo.updateInterestedTags(interestedTags, {
            if_Successful()
        }, {
            ifNotSuccessful(it)
        })
    }

    fun updateUserAccount(userName: String, userBio: String, userPicLink: String?,  if_Successful: () -> Unit, ifNotSuccessful: () -> Unit){
        return fireStoreRepo.updateUserAccount(userName, userBio, userPicLink, if_Successful, ifNotSuccessful)
    }
}