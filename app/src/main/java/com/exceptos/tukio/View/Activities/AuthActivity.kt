package com.exceptos.tukio.View.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.exceptos.tukio.Listeners.OnAuthLevelClicked
import com.exceptos.tukio.R
import com.exceptos.tukio.View.Fragments.AuthFragments.SignInFragment
import com.exceptos.tukio.View.Fragments.AuthFragments.SignUpFragment

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
            .replace(R.id.auth_activity_frame_layout,
                com.exceptos.tukio.View.Fragments.AuthFragments.PasswordRecoveryFragment()
            )
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
