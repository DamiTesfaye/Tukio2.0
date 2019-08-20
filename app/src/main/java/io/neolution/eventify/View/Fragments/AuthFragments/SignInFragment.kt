package io.neolution.eventify.View.Fragments.AuthFragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import io.neolution.eventify.Listeners.OnAuthLevelClicked
import io.neolution.eventify.R
import io.neolution.eventify.Repos.AuthRepo
import io.neolution.eventify.Utils.AppUtils
import io.neolution.eventify.View.Activities.HomeActivity

class SignInFragment: Fragment() {

    private lateinit var onAuthLevelClicked: OnAuthLevelClicked
    private val EMAILRE = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.new_signin_activity_auth, container, false )

        val signUpText = view.findViewById<TextView>(R.id.new_auth_signin_act_other_opt)
        signUpText.setOnClickListener {
            onAuthLevelClicked.goSignUp()
        }

        val emailLayout = view.findViewById<TextInputLayout>(R.id.new_auth_signin_act_email_layout)
        val emailEdit = view.findViewById<TextInputEditText>(R.id.new_auth_signin_act_email)

        val passwordLayout = view.findViewById<TextInputLayout>(R.id.new_auth_signin_act_password_layout)
        val passwordEdit = view.findViewById<TextInputEditText>(R.id.new_auth_signin_act_password)

        val signInContainer = view.findViewById<RelativeLayout>(R.id.new_auth_signin_act_signin_btn)
        val signInText = view.findViewById<TextView>(R.id.new_auth_signin_act_signin_text)
        val signInProgress = view.findViewById<ProgressBar>(R.id.new_auth_signin_act_progress)

        val forgotPassword = view.findViewById<TextView>(R.id.new_auth_signin_forgot_password)
        forgotPassword.setOnClickListener {
            onAuthLevelClicked.recoverPassword()
        }

        signInText.setOnClickListener {
            val emailText = emailEdit.text.toString().trim()
            val passwordText = passwordEdit.text.toString().trim()

            if (emailText.isNotEmpty() && passwordText.isNotEmpty()){

                val regex = Regex(EMAILRE)
                if (regex.matches(emailText)){
                    closeKeyboard()

                    signInContainer.background = ContextCompat.getDrawable(context!!, R.drawable.buttonbg_outline)
                    signInText.visibility = View.GONE
                    signInProgress.visibility = View.VISIBLE

                    AuthRepo.signIn(email = emailText, password = passwordText).addOnSuccessListener {
                        context!!.startActivity(Intent(context!!, HomeActivity::class.java))
                        activity!!.finish()

                    }.addOnFailureListener {
                        val vieW = activity!!.findViewById<View>(android.R.id.content)
                        AppUtils.getCustomSnackBar(v = vieW, context = context!!, m = it.localizedMessage!!).show()

                        signInContainer.background = ContextCompat.getDrawable(context!!, R.drawable.buttonbg)
                        signInText.visibility = View.VISIBLE
                        signInProgress.visibility = View.GONE
                    }
                }else{
                    val vieW = activity!!.findViewById<View>(android.R.id.content)
                    AppUtils.getCustomSnackBar(v = vieW, context = context!!, m = "Invalid Email Address").show()
                }

            }else{
                if (emailText.isEmpty()){
                    emailLayout.isErrorEnabled = true
                    emailLayout.error = "Please type in your email"
                }else if (passwordText.isEmpty()){
                    passwordLayout.isErrorEnabled = true
                    passwordLayout.error = "Please type in your password"
                }
            }
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        onAuthLevelClicked = context as OnAuthLevelClicked
    }

    private fun closeKeyboard(){
        val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity!!.currentFocus
        if (view == null){
            view = View(context!!)
        }

        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}