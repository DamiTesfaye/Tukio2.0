package io.neolution.eventify.View.Activities

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import io.neolution.eventify.Data.ViewModels.AuthViewModel
import io.neolution.eventify.R
import io.neolution.eventify.Utils.AppUtils

class SignUpActivity: AppCompatActivity() {

    lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_signup_activity_auth)

        viewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)
        val signInText = findViewById<TextView>(R.id.new_auth_signup_act_other_opt)
        signInText.setOnClickListener{
            startActivity(Intent(this, SignInActivity::class.java))
        }

        val nameLayout = findViewById<TextInputLayout>(R.id.new_auth_signup_act_name_layout)
        val nameEdit = findViewById<TextInputEditText>(R.id.new_auth_signup_act_name)

        val emailLayout = findViewById<TextInputLayout>(R.id.new_auth_signup_act_email_layout)
        val emailEdit = findViewById<TextInputEditText>(R.id.new_auth_signup_act_email)

        val bioEdit = findViewById<TextInputEditText>(R.id.new_auth_signup_act_bio)

        val passwordLayout = findViewById<TextInputLayout>(R.id.new_auth_signup_act_password_layout)
        val passwordEdit = findViewById<TextInputEditText>(R.id.new_auth_signup_act_password)

        val confPasswordLayout = findViewById<TextInputLayout>(R.id.new_auth_signup_act_conf_password_layout)
        val confPasswordEdit = findViewById<TextInputEditText>(R.id.new_auth_signup_act_conf_password)

        val signUpText = findViewById<TextView>(R.id.new_auth_signup_act_signup_text)
        val signUpProgress = findViewById<ProgressBar>(R.id.new_auth_signup_act_progress)
        val signUpContainer = findViewById<RelativeLayout>(R.id.new_auth_signup_act_signup_btn)

        signUpText.setOnClickListener {
            val nameText = nameEdit.text.toString().trim()
            val emailText = emailEdit.text.toString().trim()
            val bioText = bioEdit.text.toString().trim()
            val passwordText = passwordEdit.text.toString().trim()
            val confPaswordText = confPasswordEdit.text.toString().trim()

            if (nameText.isNotEmpty() && emailText.isNotEmpty() && passwordText.isNotEmpty() && confPaswordText.isNotEmpty()){

                if (passwordText == confPaswordText){
                    signUpContainer.background = ContextCompat.getDrawable(this, R.drawable.buttonbg_outline)
                    signUpText.visibility = View.GONE
                    signUpProgress.visibility = View.VISIBLE

                    viewModel.createAccount(email = emailText, userName = nameText, password = passwordText, bio = bioText, interestedTags = null,
                        userImageThumb = null, userPicLink = null, if_Not_Successful = {

                            signUpContainer.background = ContextCompat.getDrawable(this, R.drawable.buttonbg)
                            signUpText.visibility = View.VISIBLE
                            signUpProgress.visibility = View.GONE

                            val view = findViewById<View>(android.R.id.content)
                            val snackbar = AppUtils.getCustomSnackBar(view, it, this)
                            snackbar.show()

                        }, if_Successful = {
                            val intent = Intent(this, TagsActivity::class.java)
                            intent.putExtra("startedFrom", SignUpActivity::class.java.simpleName)

                            startActivity(intent)
                            finish()

                        }, whenCreatingAccount = {

                        })

                }else{
                    passwordLayout.isErrorEnabled = true
                    passwordLayout.error = "Your passwords do not match"

                    confPasswordLayout.isErrorEnabled = true
                    confPasswordLayout.error = "Your passwords do not match"
                }

            }else{
                if (nameText.isEmpty()){
                    nameLayout.isErrorEnabled = true
                    nameLayout.error = "Please input a name"
                }else if (emailText.isEmpty()){
                    emailLayout.isErrorEnabled = true
                    emailLayout.error = "Please input an email"
                }else if(passwordText.isEmpty()){
                    passwordLayout.isErrorEnabled = true
                    passwordLayout.error = "Please input a password"
                }else if(confPaswordText.isEmpty()){
                    confPasswordLayout.isErrorEnabled = true
                    confPasswordLayout.error = "Please input your password again to confirm"
                }
            }

        }
    }

}