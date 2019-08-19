package io.neolution.eventify.View.Fragments.HomeFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import io.neolution.eventify.Data.Adapters.HomeAdapter
import io.neolution.eventify.Data.ModelClasses.FullEventsModel
import io.neolution.eventify.Data.ModelClasses.breakDocumentIntoEvntsModel
import io.neolution.eventify.Data.ViewModels.EventsViewModel
import io.neolution.eventify.Listeners.OnAddReminderClicked
import io.neolution.eventify.Listeners.OnShareEventClicked
import io.neolution.eventify.R
import io.neolution.eventify.Repos.AuthRepo
import io.neolution.eventify.Repos.FireStoreRepo
import io.neolution.eventify.Utils.AppUtils

class PinnedEventsFragment: Fragment() {

    private lateinit var adapter: HomeAdapter
    private lateinit var listOfEvents: MutableList<FullEventsModel>

    private lateinit var loadingPostedEvents: ProgressBar
    private lateinit var postedNoEvents: LinearLayout
    private lateinit var firestoreRepo: FireStoreRepo

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_pinned_events, container, false)

        val shareEventListener = activity!! as OnShareEventClicked
        val onAddReminderClicked = activity!! as OnAddReminderClicked

        loadingPostedEvents = view.findViewById(R.id.loading_pinned_events)
        postedNoEvents = view.findViewById(R.id.pinned_events_no_events)
        firestoreRepo = FireStoreRepo()

        listOfEvents = mutableListOf()
        adapter = HomeAdapter(context!!, listOfEvents, activity!!, shareEventListener, onAddReminderClicked)

        val recyclerView = view.findViewById<RecyclerView>(R.id.pinned_events_recycler)
        recyclerView.run{
            layoutManager = AppUtils.getRecycleLayout(AppUtils.LINEAR_LAYOUT_MANAGER, context!!)
        }
        recyclerView.adapter = adapter

        firestoreRepo.getEventPosts().addSnapshotListener(activity!!) { eventPostsSnapshot, _ ->
            if (eventPostsSnapshot != null && !eventPostsSnapshot.isEmpty) {
                for (eventDoc in eventPostsSnapshot.documents) {
                    val eventModel = eventDoc.breakDocumentIntoEvntsModel()

                    firestoreRepo.getDocumentLikesCollection(eventDoc.id).document(AuthRepo.getUserUid()).get()
                        .addOnSuccessListener { documentTask ->
                            if (documentTask.exists()) {
                                val fullEventModel = FullEventsModel(eventModel, eventDoc.id)
                                listOfEvents.add(fullEventModel)
                                adapter.notifyDataSetChanged()

                            }
                        }
                }
            }
        }

        return view
    }

}