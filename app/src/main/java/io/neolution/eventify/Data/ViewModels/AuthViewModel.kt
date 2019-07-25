package io.neolution.eventify.Data.ViewModels

import android.arch.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import io.neolution.eventify.Repos.AuthRepo
import io.neolution.eventify.Repos.FireStoreRepo

/**
 * Created by Big-Nosed Developer on the Edge of Infinity.
 */
class AuthViewModel: ViewModel() {

    val fireStoreRepo = FireStoreRepo()

    fun signIn(email: String, password: String, if_Successful: () -> Unit
               , if_Not_Successful: (exception: String) -> Unit): Task<AuthResult>{
        return AuthRepo.signIn(email, password).addOnCompleteListener {
            if(it.isSuccessful){
                if_Successful()
            }else{
               if_Not_Successful(it.exception!!.localizedMessage)
            }
        }
    }

    fun createAccount(email: String, password: String
                      , if_Successful: () -> Unit, if_Not_Successful: (exception: String) -> Unit
                      , userName: String, bio: String
                      , interestedTags: List<String>?, whenCreatingAccount: () -> Unit, userPicLink: String?, userImageThumb: String?): Task<AuthResult>{
        return AuthRepo.signUpWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful){
                whenCreatingAccount()
                fireStoreRepo.createUserAccount(userName, email, bio, interestedTags, {
                    if_Successful()
                }, {
                    if_Not_Successful(it.exception!!.localizedMessage)
                }, userPicLink, userImageThumb)
            }else{
                if_Not_Successful(it.exception!!.localizedMessage)
            }
        }
    }


}