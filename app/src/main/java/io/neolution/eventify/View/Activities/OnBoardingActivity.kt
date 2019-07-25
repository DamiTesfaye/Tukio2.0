package io.neolution.eventify.View.Activities

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import io.neolution.eventify.Data.Adapters.OnBoardingAdapter
import io.neolution.eventify.R
import io.neolution.eventify.View.Fragments.OnBoardingFragment.NotificationFragment
import io.neolution.eventify.View.Fragments.OnBoardingFragment.PersonalisedFeedFragment
import io.neolution.eventify.View.Fragments.OnBoardingFragment.UpdatesFragment
import kotlinx.android.synthetic.main.activity_on_boarding.*

class OnBoardingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)

        val sharedPrefs = getSharedPreferences("startup_pref", Context.MODE_PRIVATE)
        val list = mutableListOf<Fragment>()

        list.add(PersonalisedFeedFragment())
        list.add(UpdatesFragment())
        list.add(NotificationFragment())

        val adapter = OnBoardingAdapter(list, supportFragmentManager)
        get_started_viewpager.adapter = adapter

        get_started_viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                when(position){
                    0 -> {
                        first_indicator.setImageDrawable(ContextCompat.getDrawable(this@OnBoardingActivity,
                            R.drawable.selected_indicator))
                        second_indicator.setImageDrawable(ContextCompat.getDrawable(this@OnBoardingActivity,
                            R.drawable.unselected_indicator))
                        third_indicator.setImageDrawable(ContextCompat.getDrawable(this@OnBoardingActivity,
                            R.drawable.unselected_indicator))
                    }

                    1 -> {
                        first_indicator.setImageDrawable(ContextCompat.getDrawable(this@OnBoardingActivity,
                            R.drawable.unselected_indicator))
                        second_indicator.setImageDrawable(ContextCompat.getDrawable(this@OnBoardingActivity,
                            R.drawable.selected_indicator))
                        third_indicator.setImageDrawable(ContextCompat.getDrawable(this@OnBoardingActivity,
                            R.drawable.unselected_indicator))
                    }

                    2 -> {
                        first_indicator.setImageDrawable(ContextCompat.getDrawable(this@OnBoardingActivity,
                            R.drawable.unselected_indicator))
                        second_indicator.setImageDrawable(ContextCompat.getDrawable(this@OnBoardingActivity,
                            R.drawable.unselected_indicator))
                        third_indicator.setImageDrawable(ContextCompat.getDrawable(this@OnBoardingActivity,
                            R.drawable.selected_indicator))
                    }
                }
            }

        })

        get_started_button.setOnClickListener {
            val editor = sharedPrefs.edit()
            editor.putString("startup", "started")
            val saved = editor.commit()

            if (saved){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(Intent(this, SignUpActivity::class.java))
                }else{
                    startActivity(Intent(this, SignUpActivity::class.java))
                }
                finish()
            }

        }

    }
}
