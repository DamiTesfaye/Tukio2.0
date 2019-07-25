package io.neolution.eventify.View.Activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import io.neolution.eventify.R
import kotlinx.android.synthetic.main.activity_add_event_prem.*

class AddEventPremActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event_prem)

        add_event_prem_almost_done_btn.setOnClickListener {
            startActivity(Intent(this, AddEventFinalActivity::class.java))
        }
    }
}
