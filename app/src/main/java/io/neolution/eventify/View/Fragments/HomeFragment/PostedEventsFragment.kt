package io.neolution.eventify.View.Fragments.HomeFragment

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import io.neolution.eventify.Data.Adapters.HomeAdapter
import io.neolution.eventify.Data.ModelClasses.FullEventsModel
import io.neolution.eventify.Data.ModelClasses.breakDocumentIntoEvntsModel
import io.neolution.eventify.Data.ViewModels.EventsViewModel
import io.neolution.eventify.Listeners.OnAddReminderClicked
import io.neolution.eventify.Listeners.OnShareEventClicked
import io.neolution.eventify.R
import io.neolution.eventify.Repos.AuthRepo
import io.neolution.eventify.Utils.AppUtils
import kotlinx.android.synthetic.main.activity_posted_events.*

class PostedEventsFragment: Fragment() {

    private lateinit var adapter: HomeAdapter
    private lateinit var listOfEvents: MutableList<FullEventsModel>

    private lateinit var loadingPostedEvents: ProgressBar
    private lateinit var postedNoEvents: LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_posted_events, container, false)

        val shareEventListener = activity!! as OnShareEventClicked
        val onAddReminderClicked = activity!! as OnAddReminderClicked

        loadingPostedEvents = view.findViewById(R.id.loading_posted_events)
        postedNoEvents = view.findViewById(R.id.posted_events_no_events)

        listOfEvents = mutableListOf()
        adapter = HomeAdapter(context!!, listOfEvents, activity!!, shareEventListener, onAddReminderClicked)

        val recyclerView = view.findViewById<RecyclerView>(R.id.posted_events_recycler)
        recyclerView.run{
            layoutManager = AppUtils.getRecycleLayout(AppUtils.LINEAR_LAYOUT_MANAGER, context!!)
        }

        recyclerView.adapter = adapter

        val eventViewModel = ViewModelProviders.of(this).get(EventsViewModel::class.java)

        eventViewModel.getEventDocuments().whereEqualTo("userUID", AuthRepo.getUserUid())
            .addSnapshotListener { snapshot, _ ->

                if (snapshot != null && !snapshot.isEmpty){

                    for (document in snapshot.documents) {

                        val eventModel = document.breakDocumentIntoEvntsModel()
                        val documentID = document.id

                        loadingPostedEvents.visibility = View.GONE
                        listOfEvents.add(FullEventsModel(eventModel, documentID))
                        adapter.notifyDataSetChanged()

                    }

                }else{

                    loadingPostedEvents.visibility = View.GONE
                    postedNoEvents.visibility = View.VISIBLE

                }


        }

        return view
    }

}