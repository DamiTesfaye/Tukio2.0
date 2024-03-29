package com.exceptos.tukio.View.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.exceptos.tukio.R
import com.exceptos.tukio.View.Fragments.OnBoardingFragment.NotificationFragment
import com.exceptos.tukio.View.Fragments.OnBoardingFragment.PersonalisedFeedFragment
import com.exceptos.tukio.View.Fragments.OnBoardingFragment.TrendsFragment
import com.exceptos.tukio.View.Fragments.OnBoardingFragment.UpdatesFragment
import kotlinx.android.synthetic.main.activity_on_boarding.*

class OnBoardingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)

        val sharedPrefs = getSharedPreferences("startup_pref", Context.MODE_PRIVATE)
        val list = mutableListOf<Fragment>()

        list.add(NotificationFragment())
        list.add(PersonalisedFeedFragment())
        list.add(UpdatesFragment())

        val adapter = com.exceptos.tukio.Data.Adapters.OnBoardingAdapter(list, supportFragmentManager)
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
                    3 -> {
                        first_indicator.setImageDrawable(ContextCompat.getDrawable(this@OnBoardingActivity,
                            R.drawable.unselected_indicator))
                        second_indicator.setImageDrawable(ContextCompat.getDrawable(this@OnBoardingActivity,
                            R.drawable.unselected_indicator))
                        third_indicator.setImageDrawable(ContextCompat.getDrawable(this@OnBoardingActivity,
                            R.drawable.unselected_indicator))
                    }
                }
            }

        })

        get_started_button.setOnClickListener {
            val editor = sharedPrefs.edit()
            editor.putString("startup", "started")
            val saved = editor.commit()

            if (saved){
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
            }

        }

    }
}
