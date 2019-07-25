package io.neolution.eventify.View.Activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import io.neolution.eventify.R
import io.neolution.eventify.Repos.AuthRepo
import io.neolution.eventify.Utils.AppUtils

class SignInActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_signin_activity_auth)

        val signUpText = findViewById<TextView>(R.id.new_auth_signin_act_other_opt)
        signUpText.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        val emailLayout = findViewById<TextInputLayout>(R.id.new_auth_signin_act_email_layout)
        val emailEdit = findViewById<TextInputEditText>(R.id.new_auth_signin_act_email)

        val passwordLayout = findViewById<TextInputLayout>(R.id.new_auth_signin_act_password_layout)
        val passwordEdit = findViewById<TextInputEditText>(R.id.new_auth_signin_act_password)

        val signInContainer = findViewById<RelativeLayout>(R.id.new_auth_signin_act_signin_btn)
        val signInText = findViewById<TextView>(R.id.new_auth_signin_act_signin_text)
        val signInProgress = findViewById<ProgressBar>(R.id.new_auth_signin_act_progress)

        signInText.setOnClickListener {
            val emailText = emailEdit.text.toString().trim()
            val passwordText = passwordEdit.text.toString().trim()

            if (emailText.isNotEmpty() && passwordText.isNotEmpty()){

                signInContainer.background = ContextCompat.getDrawable(this, R.drawable.buttonbg_outline)
                signInText.visibility = GONE
                signInProgress.visibility = VISIBLE

                AuthRepo.signIn(email = emailText, password = passwordText).addOnSuccessListener {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()

                }.addOnFailureListener {
                    val view = findViewById<View>(android.R.id.content)
                    AppUtils.getCustomSnackBar(v = view, context = this, m = it.localizedMessage).show()

                    signInContainer.background = ContextCompat.getDrawable(this, R.drawable.buttonbg)
                    signInText.visibility = VISIBLE
                    signInProgress.visibility = GONE
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
    }

}