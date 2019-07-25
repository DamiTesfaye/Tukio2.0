package io.neolution.eventify.View.Fragments.AuthFragment

import android.app.ActivityOptions
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import io.neolution.eventify.Data.ModelClasses.indicate
import io.neolution.eventify.Data.ViewModels.AuthViewModel
import io.neolution.eventify.R
import io.neolution.eventify.Utils.AppUtils
import io.neolution.eventify.View.Activities.TagsActivity
import io.neolution.eventify.databinding.FragmentSignupBinding

/**
 * Created by Big-Nosed Developer on the Edge of Infinity.
 */
class SignupFragments: Fragment() {
    lateinit var binding: FragmentSignupBinding
    lateinit var viewModel: AuthViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup, container!!, false)
         viewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)

        if(savedInstanceState != null){

            val bio = savedInstanceState.getString("bio")
            val email = savedInstanceState.getString("email")
            val password = savedInstanceState.getString("password")
            val confamPassword = savedInstanceState.getString("confamPassword")
            val userName = savedInstanceState.getString("userName")

            if(!bio.isNullOrEmpty() && !email.isNullOrEmpty()
                && !password.isNullOrEmpty()  && !confamPassword.isNullOrEmpty()
                && !userName.isNullOrEmpty()){

                binding.apply {
                    fragmentSignupBio.setText(bio)
                    fragmentSignupConfamPasssword.setText(confamPassword)
                    fragmentSignupEmail.setText(email)
                    fragmentSignupPasssword.setText(password)
                    fragmentSignupUsername.setText(userName)
                }

            }
        }

        binding.fragmentSignupButton.setOnClickListener {

            if(binding.fragmentSignupConfamPasssword.text.toString() == binding.fragmentSignupPasssword.text.toString()
                    && !binding.fragmentSignupBio.isEmpty() && !binding.fragmentSignupConfamPasssword.isEmpty()
                && !binding.fragmentSignupEmail.isEmpty() && !binding.fragmentSignupPasssword.isEmpty()
                && !binding.fragmentSignupUsername.isEmpty()

            ){


                val dialog = AppUtils.instantiateProgressDialog("We're signing you up.. Please wait!", context!!)
                dialog.show()

                viewModel.createAccount(binding.fragmentSignupEmail.text.toString(), binding.fragmentSignupConfamPasssword.text.toString(),
                    {

                        val intent = Intent(context, TagsActivity::class.java)
                        intent.putExtra("startedFrom", SignupFragments::class.java.simpleName)

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity!!).toBundle())
                        }else{
                            startActivity(intent)
                        }
                        dialog.dismiss()

                        activity!!.finish()
                    },
                    {
                        exception ->

                        dialog.dismiss()
                        context?.indicate(exception)
                    },
                    binding.fragmentSignupUsername.text.toString(),
                    binding.fragmentSignupBio.text.toString(),
                    null, {
                        dialog.setMessage("Setting Up Account...")
                    }, null, null)
                }else{

                context?.indicate("Please fill in all fields!")
            }

            }

        return binding.root
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        val fragmentVisisble = context as SigninFragment.FragmentVisible


        fragmentVisisble.onSignUpFragmentVisisble()
    }

    fun EditText.isEmpty(): Boolean{
        return this.text.isNullOrEmpty()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.apply {
            putString("bio", binding.fragmentSignupBio.text.toString())
            putString("email", binding.fragmentSignupEmail.text.toString())
            putString("password", binding.fragmentSignupPasssword.text.toString())
            putString("confamPassword", binding.fragmentSignupConfamPasssword.text.toString())
            putString("username", binding.fragmentSignupUsername.text.toString())
        }

    }


}

