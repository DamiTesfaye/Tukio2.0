package io.neolution.eventify.View.Activities

import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import io.neolution.eventify.View.Fragments.AuthFragment.SigninFragment
import io.neolution.eventify.View.Fragments.AuthFragment.SignupFragments
import io.neolution.eventify.R
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity(), SigninFragment.FragmentVisible {
    override fun onSignInFragmentVisible() {

        //Setting the label of the current auth state
        //TODO: DEBUG ON ANDROID 4.4
        if (auth_signup != null) {
            auth_signup.setOnClickListener {

                manager.beginTransaction().replace(
                    R.id.auth_frame_layout,
                    SignupFragments()
                ).commit()

                auth_signup.text = "Already have an account? Sign In!"
            }
        }
    }

    override fun onSignUpFragmentVisisble() {
        //Setting the label of the current auth state

        if (auth_signup != null) {
            auth_signup.setOnClickListener {

                manager.beginTransaction().addToBackStack(null).replace(
                    R.id.auth_frame_layout,
                    SigninFragment()
                ).commit()

                auth_signup.text = "Don't have an account? Sign Up!"
            }
        }
    }

    lateinit var manager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        FirebaseAuth.getInstance().currentUser

        //Makes the activity full screen
        val window = window
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        manager = supportFragmentManager

        //Preliminary the  tying showing fragment
        manager.beginTransaction().addToBackStack(null).replace(
            R.id.auth_frame_layout,
            SigninFragment()
        ).commit()


    }

}
