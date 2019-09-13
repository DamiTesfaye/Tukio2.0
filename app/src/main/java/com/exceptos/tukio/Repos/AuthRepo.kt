package com.exceptos.tukio.Repos

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/**
 * Created by Big-Nosed Developer on the Edge of Infinity.
 */
object AuthRepo {

    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun getAuthInstance(): FirebaseAuth{
        return mAuth
    }

    fun signIn(email: String, password: String): Task<AuthResult> {
       return mAuth.signInWithEmailAndPassword(email, password)
    }

    fun signUpWithEmailAndPassword(email: String, password: String): Task<AuthResult>{
        return mAuth.createUserWithEmailAndPassword(email, password)
    }

    fun getCurrentUser(): FirebaseUser?{
        return FirebaseAuth.getInstance().currentUser
    }

    fun getUserUid(): String{
        return if (getCurrentUser() != null){
            getCurrentUser()!!.uid
        }else{
            ""
        }

    }

    fun signOut(){
        mAuth.signOut()
    }
}