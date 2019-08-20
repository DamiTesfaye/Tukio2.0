package io.neolution.eventify.View.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.neolution.eventify.Listeners.OnAuthLevelClicked
import io.neolution.eventify.R
import io.neolution.eventify.View.Fragments.AuthFragments.PasswordRecoveryFragment
import io.neolution.eventify.View.Fragments.AuthFragments.SignInFragment
import io.neolution.eventify.View.Fragments.AuthFragments.SignUpFragment

class AuthActivity : AppCompatActivity(), OnAuthLevelClicked {

    override fun goSignUp() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.auth_activity_frame_layout, SignUpFragment())
            .commitAllowingStateLoss()
    }

    override fun goSignIn() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.auth_activity_frame_layout, SignInFragment())
            .commitAllowingStateLoss()
    }

    override fun recoverPassword() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.auth_activity_frame_layout, PasswordRecoveryFragment())
            .commitAllowingStateLoss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        supportFragmentManager.beginTransaction()
            .replace(R.id.auth_activity_frame_layout, SignInFragment())
            .commitAllowingStateLoss()

    }
}
