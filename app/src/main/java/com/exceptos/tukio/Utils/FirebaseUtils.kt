package com.exceptos.tukio.Utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.exceptos.tukio.Data.ModelClasses.EventsModel
import com.exceptos.tukio.Data.ModelClasses.UserModel

/**
 * Created by Big-Nosed Developer on the Edge of Infinity.
 */
class FirebaseUtils {

    private val sharePrefUIDPath: String = "u_id"
    private val defaultSharePrefPath: String = "default_sharedPrefs"

    companion object {
        val userCollectionPath: String ="users"
        val eventsCollectionPath: String = "Events"
        val goingCollectionPath = "Going"
        val promotedCollectionPath = "Promoted Events"
        val updatesCollectionPath = "Updates"
        val clickedCollectionPath = "Clicked"
        val betaClickedCollectionPath = "Beta_Clicked"
        val betaEventsCollectionPath = "Beta_Events"
        val betaPromotedCollectionPath = "Beta_Promoted"
    }

    fun getDefaultPreferences(context: Context) = context.getSharedPreferences(defaultSharePrefPath, MODE_PRIVATE)

    fun saveUserUID(uId: String, context: Context){

        val sharePreferences = getDefaultPreferences(context)

        val editor = sharePreferences.edit()
        editor.putString(sharePrefUIDPath, uId)
        editor.apply()
    }

    fun getUserUID(context: Context): String{
        return getDefaultPreferences(context).getString(sharePrefUIDPath, "")!!
    }

    fun saveUserDetails(user: UserModel?, context: Context){

        val sharePreferences = getDefaultPreferences(context)
        val editor = sharePreferences.edit()

        if(user != null){
            editor.putString("userName", user.userName)
            editor.putString("userBio", user.userBio)
            editor.putString("userImage", user.userImage)
        }

        editor.apply()
    }

}