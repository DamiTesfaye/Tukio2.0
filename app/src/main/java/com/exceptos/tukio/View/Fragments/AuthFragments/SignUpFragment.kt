package com.exceptos.tukio.View.Fragments.AuthFragments

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
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.exceptos.tukio.Data.ViewModels.AuthViewModel
import com.exceptos.tukio.Listeners.OnAuthLevelClicked
import com.exceptos.tukio.R
import com.exceptos.tukio.Utils.AppUtils
import com.exceptos.tukio.View.Activities.AuthActivity
import com.exceptos.tukio.View.Activities.TagsActivity

class SignUpFragment: Fragment() {

    lateinit var viewModel: AuthViewModel
    private lateinit var onAuthLevelClicked: OnAuthLevelClicked
    private val EMAILRE = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.new_signup_activity_auth, container, false)

        viewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)
        val signInText = view.findViewById<TextView>(R.id.new_auth_signup_act_other_opt)
        signInText.setOnClickListener{
            onAuthLevelClicked.goSignIn()
        }

        val nameLayout = view.findViewById<TextInputLayout>(R.id.new_auth_signup_act_name_layout)
        val nameEdit = view.findViewById<TextInputEditText>(R.id.new_auth_signup_act_name)

        val emailLayout = view.findViewById<TextInputLayout>(R.id.new_auth_signup_act_email_layout)
        val emailEdit = view.findViewById<TextInputEditText>(R.id.new_auth_signup_act_email)

        val bioEdit = view.findViewById<TextInputEditText>(R.id.new_auth_signup_act_bio)

        val passwordLayout = view.findViewById<TextInputLayout>(R.id.new_auth_signup_act_password_layout)
        val passwordEdit = view.findViewById<TextInputEditText>(R.id.new_auth_signup_act_password)

        val confPasswordLayout = view.findViewById<TextInputLayout>(R.id.new_auth_signup_act_conf_password_layout)
        val confPasswordEdit = view.findViewById<TextInputEditText>(R.id.new_auth_signup_act_conf_password)

        val signUpText = view.findViewById<TextView>(R.id.new_auth_signup_act_signup_text)
        val signUpProgress = view.findViewById<ProgressBar>(R.id.new_auth_signup_act_progress)
        val signUpContainer = view.findViewById<RelativeLayout>(R.id.new_auth_signup_act_signup_btn)

        signUpText.setOnClickListener {
            val nameText = nameEdit.text.toString().trim()
            val emailText = emailEdit.text.toString().trim()
            val bioText = bioEdit.text.toString().trim()
            val passwordText = passwordEdit.text.toString().trim()
            val confPaswordText = confPasswordEdit.text.toString().trim()

            if (nameText.isNotEmpty() && emailText.isNotEmpty() && passwordText.isNotEmpty() && confPaswordText.isNotEmpty()){

                val regex = Regex(EMAILRE)
                if (regex.matches(emailText)){
                    if (passwordText == confPaswordText){

                        closeKeyboard()

                        signUpContainer.background = ContextCompat.getDrawable(context!!, R.drawable.buttonbg_outline)
                        signUpText.visibility = View.GONE
                        signUpProgress.visibility = View.VISIBLE

                        viewModel.createAccount(email = emailText, userName = nameText, password = passwordText, bio = bioText, interestedTags = null,
                            userImageThumb = null, userPicLink = null, if_Not_Successful = {

                                signUpContainer.background = ContextCompat.getDrawable(context!!, R.drawable.buttonbg)
                                signUpText.visibility = View.VISIBLE
                                signUpProgress.visibility = View.GONE

                                val vieW = activity!!.findViewById<View>(android.R.id.content)
                                val snackbar = AppUtils.getCustomSnackBar(vieW, it, context!!)
                                snackbar.show()

                            }, if_Successful = {
                                val intent = Intent(context!!, TagsActivity::class.java)
                                intent.putExtra("startedFrom", AuthActivity::class.java.simpleName)

                                startActivity(intent)
                                activity!!.finish()

                            }, whenCreatingAccount = {

                            })

                    }else{
                        passwordLayout.isErrorEnabled = true
                        passwordLayout.error = "Your passwords do not match"

                        confPasswordLayout.isErrorEnabled = true
                        confPasswordLayout.error = "Your passwords do not match"
                    }
                }else{
                    val vieW = activity!!.findViewById<View>(android.R.id.content)
                    val snackbaR = AppUtils.getCustomSnackBar(vieW, "Invalid Email Address", context!!)
                    snackbaR.show()
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