package com.exceptos.tukio.View.Fragments.HomeFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.exceptos.tukio.Data.ModelClasses.FullEventsModel
import com.exceptos.tukio.Data.ModelClasses.breakDocumentIntoEvntsModel
import com.exceptos.tukio.Data.ViewModels.EventsViewModel
import com.exceptos.tukio.Listeners.OnAddReminderClicked
import com.exceptos.tukio.Listeners.OnShareEventClicked
import com.exceptos.tukio.R
import com.exceptos.tukio.Repos.AuthRepo
import com.exceptos.tukio.Repos.FireStoreRepo
import com.exceptos.tukio.Utils.AppUtils

class PinnedEventsFragment: Fragment() {

    private lateinit var adapter: com.exceptos.tukio.Data.Adapters.HomeAdapter
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
        adapter = com.exceptos.tukio.Data.Adapters.HomeAdapter(
            context!!,
            listOfEvents,
            activity!!,
            shareEventListener,
            onAddReminderClicked
        )

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

                                    postedNoEvents.visibility = GONE
                                    loadingPostedEvents.visibility = GONE

                                }
                            }
                }
            }

            if (listOfEvents.isEmpty()){
                postedNoEvents.visibility = VISIBLE
                loadingPostedEvents.visibility = GONE
            }
        }

        return view
    }

}