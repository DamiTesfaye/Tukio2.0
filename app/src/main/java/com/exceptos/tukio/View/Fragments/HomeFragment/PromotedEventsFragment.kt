package com.exceptos.tukio.View.Fragments.HomeFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.exceptos.tukio.Data.ModelClasses.FullEventsModel
import com.exceptos.tukio.Data.ModelClasses.breakDocumentIntoEvntsModel
import com.exceptos.tukio.Data.ViewModels.EventsViewModel
import com.exceptos.tukio.Listeners.OnAddReminderClicked
import com.exceptos.tukio.Listeners.OnShareEventClicked
import com.exceptos.tukio.R
import com.exceptos.tukio.Repos.AuthRepo
import com.exceptos.tukio.Utils.AppUtils

class PromotedEventsFragment: Fragment() {

    private lateinit var adapter: com.exceptos.tukio.Data.Adapters.HomeAdapter
    private lateinit var listOfEvents: MutableList<FullEventsModel>

    private lateinit var loadingPostedEvents: ProgressBar
    private lateinit var postedNoEvents: LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_promoted_events, container, false)

        val shareEventListener = activity!! as OnShareEventClicked
        val onAddReminderClicked = activity!! as OnAddReminderClicked

        loadingPostedEvents = view.findViewById(R.id.loading_promoted_events)
        postedNoEvents = view.findViewById(R.id.promoted_events_no_events)

        listOfEvents = mutableListOf()
        adapter = com.exceptos.tukio.Data.Adapters.HomeAdapter(
            context!!,
            listOfEvents,
            activity!!,
            shareEventListener,
            onAddReminderClicked
        )

        val recyclerView = view.findViewById<RecyclerView>(R.id.promoted_events_recycler)
        recyclerView.run{
            layoutManager = AppUtils.getRecycleLayout(AppUtils.LINEAR_LAYOUT_MANAGER, context!!)
        }
        recyclerView.adapter = adapter

        val eventViewModel = ViewModelProviders.of(this).get(EventsViewModel::class.java)
        eventViewModel.getPromotedEvents().whereEqualTo("userUID", AuthRepo.getUserUid())
            .addSnapshotListener { snapshot, _ ->

                if (snapshot != null && !snapshot.isEmpty){

                    for (document in snapshot.documents) {
                        val eventModel = document.breakDocumentIntoEvntsModel()
                        val documentID = document.id

                        loadingPostedEvents.visibility = View.GONE
                        listOfEvents.add(FullEventsModel(eventModel, documentID))
                        adapter.notifyDataSetChanged()
                    }

                    if(listOfEvents.isEmpty()){
                        loadingPostedEvents.visibility = View.GONE
                        postedNoEvents.visibility = View.VISIBLE
                    }

                }else{

                    loadingPostedEvents.visibility = View.GONE
                    postedNoEvents.visibility = View.VISIBLE
                }
            }

        return view
    }

}