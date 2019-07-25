package io.neolution.eventify.View.Activities

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageButton
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import io.neolution.eventify.Data.Adapters.HomeAdapter
import io.neolution.eventify.Data.ModelClasses.FullEventsModel
import io.neolution.eventify.Data.ModelClasses.breakDocumentIntoEvntsModel
import io.neolution.eventify.Data.ViewModels.EventsViewModel
import io.neolution.eventify.Listeners.OnShareEventClicked
import io.neolution.eventify.R
import io.neolution.eventify.Repos.AuthRepo
import io.neolution.eventify.Services.GoogleServicesClass
import io.neolution.eventify.Utils.AppUtils
import kotlinx.android.synthetic.main.activity_promoted_events.*

class PromotedEventsActivity : AppCompatActivity(), OnShareEventClicked {

    override fun onShareButtonClick(eventTitle: String, eventId: String, eventLocation: String, eventDate: String) {

    }

    private lateinit var adapter: HomeAdapter
    private lateinit var listOfEvents: MutableList<FullEventsModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promoted_events)

        listOfEvents = mutableListOf()
        adapter = HomeAdapter(this, listOfEvents, this, this)

        val recyclerView = findViewById<RecyclerView>(R.id.promoted_events_recycler)
        recyclerView.run{
            layoutManager = AppUtils.getRecycleLayout(AppUtils.LINEAR_LAYOUT_MANAGER, this@PromotedEventsActivity)
        }

        recyclerView.adapter = adapter

        val eventViewModel = ViewModelProviders.of(this).get(EventsViewModel::class.java)

        findViewById<ImageButton>(R.id.close_promoted_events_activity).setOnClickListener {
            onBackPressed()
        }


        eventViewModel.getPromotedEvents().whereEqualTo("userUID", AuthRepo.getUserUid())
            .addSnapshotListener { snapshot, _ ->

                if (snapshot != null && !snapshot.isEmpty){

                    for (document in snapshot.documents) {

                        val eventModel = document.breakDocumentIntoEvntsModel()
                        val documentID = document.id

                        loading_promoted_events.visibility = GONE
                        listOfEvents.add(FullEventsModel(eventModel, documentID))
                        adapter.notifyDataSetChanged()

                    }

                }else{

                    loading_promoted_events.visibility = GONE
                    promoted_events_no_events.visibility = VISIBLE

                }

            }
    }
}
