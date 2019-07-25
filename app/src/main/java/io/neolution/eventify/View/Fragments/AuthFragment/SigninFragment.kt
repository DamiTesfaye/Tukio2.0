package io.neolution.eventify.View.Fragments.AuthFragment

import android.app.ActivityOptions
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import io.neolution.eventify.Data.ModelClasses.indicate
import io.neolution.eventify.Data.ViewModels.AuthViewModel
import io.neolution.eventify.R
import io.neolution.eventify.Utils.AppUtils
import io.neolution.eventify.View.Activities.HomeActivity
import kotlinx.android.synthetic.main.fragment_signin.*

/**
 * Created by Big-Nosed Developer on the Edge of Infinity.
 */
class SigninFragment: Fragment(), View.OnClickListener {

    fun TextView.isEmpty(): Boolean{
        return TextUtils.isEmpty(this.text)
    }


    interface FragmentVisible{
        fun onSignInFragmentVisible()

        fun onSignUpFragmentVisisble()
    }


    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.fragment_signin_button -> {

                if(!fragment_signin_email.isEmpty() && !fragment_signin_password.isEmpty()){

                   val dialog = AppUtils.instantiateProgressDialog("Signing In..", context!!)
                    dialog.show()

                   viewModel.signIn(fragment_signin_email.text.toString(), fragment_signin_password.text.toString(), {
                       dialog.dismiss()
                       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                           startActivity(Intent(activity, HomeActivity::class.java), ActivityOptions.makeSceneTransitionAnimation(activity!!).toBundle())
                       }else{
                           startActivity(Intent(activity, HomeActivity::class.java))
                       }
                       activity!!.finish()
                   }, {exception ->
                       dialog.dismiss()
                       context?.indicate(exception)
                   })

                }else{
                    context?.indicate(AppUtils.fillInAllFieldsError)

                }
            }
        }
    }


    lateinit var button: Button
    lateinit var viewModel: AuthViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_signin, container!!, false)
        button = view.findViewById(R.id.fragment_signin_button)
        viewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)

        if(savedInstanceState != null){
            val email = savedInstanceState.getString("email")
            val password = savedInstanceState.getString("password")

            if(!email.isNullOrEmpty() && !password.isNullOrEmpty()){
                fragment_signin_email.setText(email)
                fragment_signin_password.setText(password)
            }
        }


        button.setOnClickListener (this@SigninFragment)
        return view
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)

        val fragmentVisible = context as FragmentVisible
        fragmentVisible.onSignInFragmentVisible()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        if (fragment_signin_email != null && fragment_signin_password != null){
            outState.apply {
                putString("email", fragment_signin_email.text.toString())
                putString("password", fragment_signin_password.text.toString())
            }
        }

    }
}

