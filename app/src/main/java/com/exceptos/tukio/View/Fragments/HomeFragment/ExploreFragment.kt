package com.exceptos.tukio.View.Fragments.HomeFragment

import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.exceptos.tukio.Data.ModelClasses.*
import com.exceptos.tukio.Data.ViewModels.EventsViewModel
import com.exceptos.tukio.Listeners.OnAddReminderClicked
import com.exceptos.tukio.Listeners.OnEventTypeSelected
import com.exceptos.tukio.Listeners.OnHomeFragmentsAttached
import com.exceptos.tukio.Listeners.OnShareEventClicked
import com.exceptos.tukio.R
import com.exceptos.tukio.Utils.AppUtils
import com.exceptos.tukio.View.Activities.AddEventPremActivity
import com.exceptos.tukio.View.Activities.SearchActivity
import com.exceptos.tukio.databinding.NewExploreLayoutBinding
import kotlinx.android.synthetic.main.new_explore_layout.*
import java.util.*


/**
 * Created by Big-Nosed Developer on the Edge of Infinity.
 */
class ExploreFragment: Fragment(), OnEventTypeSelected{

    override fun onEventTypeClicked(eventType: String) {

        onHomeFragmentsAttached.onEventTypeSelected(eventType)

        loadEvents(eventType)
        currentEventType = eventType

    }

    lateinit var binding: NewExploreLayoutBinding
    lateinit var eventsViewModel: EventsViewModel
    private lateinit var eventRecycler: RecyclerView

    private lateinit var exploreLoadingLayout: LinearLayout
    private lateinit var exploreEmptyLayout: LinearLayout

    private lateinit var exploreLoadingText: TextView
    private lateinit var exploreEmptyTextView: TextView
    private lateinit var exploreSearchContainer: RelativeLayout

    private lateinit var onHomeFragmentsAttached: OnHomeFragmentsAttached
    lateinit var homeAdapter: com.exceptos.tukio.Data.Adapters.HomeAdapter
    lateinit var listOfEvent : MutableList<FullEventsModel>
    lateinit var lastDocumentSnapshot: DocumentSnapshot
    private var alreadyLoaded = true
    private var currentEventType = "Tech"

    private lateinit var shareEventClicked: OnShareEventClicked
    private lateinit var onAddReminderClicked: OnAddReminderClicked

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        //Initializing
        binding = DataBindingUtil.inflate(inflater, R.layout.new_explore_layout, container!!, false)

        eventRecycler = binding.root.findViewById(R.id.new_explore_event_recycler)
        exploreLoadingLayout = binding.root.findViewById(R.id.new_explore_progress_layout)
        exploreLoadingText = binding.root.findViewById(R.id.new_explore_progress_text)

        exploreEmptyLayout = binding.root.findViewById(R.id.new_explore_empty_layout)
        exploreEmptyTextView = binding.root.findViewById(R.id.new_explore_empty_text)
        exploreSearchContainer = binding.root.findViewById(R.id.new_explore_search_container)

        exploreSearchContainer.setOnClickListener {
            startActivity(Intent(context!!, SearchActivity::class.java))
        }

        eventRecycler.setHasFixedSize(true)
        eventRecycler.layoutManager = LinearLayoutManager(context!!)
        val recyclerView = binding.root.findViewById<RecyclerView>(R.id.new_explore_tag_recycler)
        recyclerView.setHasFixedSize(true)

        val chipList = AppUtils.createChipList()

        val adapter = com.exceptos.tukio.Data.Adapters.EventTypeAdapter(chipList, context!!, this)
        val manager = StaggeredGridLayoutManager(
            1,
            StaggeredGridLayoutManager.HORIZONTAL
        )
        manager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE

        recyclerView.layoutManager = manager
        recyclerView.adapter = adapter
        eventsViewModel = ViewModelProviders.of(this).get(EventsViewModel::class.java)


        shareEventClicked = activity!! as OnShareEventClicked
        onAddReminderClicked = activity!! as OnAddReminderClicked

        eventRecycler.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (!recyclerView.canScrollVertically(1)){
                    loadMoreEvents()
                }
            }
        })

        //TODO: Return the alreadyLoaded variable to a global variable


        return binding.root
    }

    override fun onStart() {
        super.onStart()

        onHomeFragmentsAttached = activity!! as OnHomeFragmentsAttached
        onHomeFragmentsAttached.onExploreFragmentAttached()
    }

    private fun loadEvents( eventType: String){
        new_explore_check_tags_layout.visibility = GONE
        exploreEmptyLayout.visibility = GONE
        eventRecycler.visibility = GONE
        exploreLoadingLayout.visibility = VISIBLE
        exploreLoadingText.text = ("Loading $eventType events..")

        populateEvents(eventType)
    }

    private fun populateEvents( eventType: String) {

        listOfEvent = mutableListOf()
        homeAdapter = com.exceptos.tukio.Data.Adapters.HomeAdapter(
            context!!,
            listOfEvent,
            activity!!,
            shareEventClicked,
            onAddReminderClicked
        )
        eventRecycler.adapter = homeAdapter

        eventsViewModel.getPromotedEvents().orderBy("eventPostTime", Query.Direction.DESCENDING).addSnapshotListener(activity!!) { snapshot, _ ->

            if (snapshot != null && !snapshot.isEmpty) {

                val promotedEventList = mutableListOf<FullEventsModel>()
                val selectedtPromotedEvents = mutableListOf<FullEventsModel>()
                val finalPromotedEventsList: List<FullEventsModel>

                for (document in snapshot.documents){
                    val eventsModel = document.breakDocumentIntoEvntsModel()
                    val id = document.id

                    if (eventsModel.eventTags[0] == eventType){
                        promotedEventList.add(FullEventsModel(eventsModel, id))
                    }
                }

                if (promotedEventList.size > 1){
                    val number1 = Random().nextInt(promotedEventList.size)
                    var number2 = Random().nextInt(promotedEventList.size)

                    while (number2 == number1){
                        number2 = Random().nextInt(promotedEventList.size)
                    }

                    selectedtPromotedEvents.add(promotedEventList[number1])
                    selectedtPromotedEvents.add(promotedEventList[number2])

                    finalPromotedEventsList = selectedtPromotedEvents.compareAccordingToMoneyPaid()
                    Log.e(ExploreFragment::class.java.simpleName, finalPromotedEventsList.toString())

                }else{
                    finalPromotedEventsList = promotedEventList
                }

                eventsViewModel.getEventDocuments().orderBy("eventPostTime", Query.Direction.DESCENDING).limit(6).addSnapshotListener (activity!!) { snapshot2, _ ->

                    if (snapshot2 != null && !snapshot2.isEmpty){

                        if (alreadyLoaded) {
                            lastDocumentSnapshot = snapshot2.documents[(snapshot2.size() - 1)]
                        }

                        for (change in snapshot2.documentChanges) {
                            if (change.type == DocumentChange.Type.ADDED){
                                val eventModel = change.document.breakDocumentIntoEvntsModel()
                                val documentID = change.document.id

                                if (alreadyLoaded){
                                    if (!listOfEvent.contains(FullEventsModel(eventModel, documentID))){
                                            if (eventModel.eventTags.contains(eventType)){
                                                listOfEvent.add(FullEventsModel(eventModel, documentID))
                                                homeAdapter.notifyDataSetChanged()
                                            }

                                    }

                                }else{

                                    if (!listOfEvent.contains(FullEventsModel(eventModel, documentID))){
                                        if (!eventModel.eventTitle.startsWith("--beta--", true) && !eventModel.eventTitle.endsWith("--beta--", true)){
                                            if (eventModel.eventTags.contains(eventType)){
                                                listOfEvent.add(0, FullEventsModel(eventModel, documentID))
                                                homeAdapter.notifyDataSetChanged()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        alreadyLoaded = false
                    }

                    if (!listOfEvent.isEmpty()){
                        Log.e(ExploreFragment::class.java.simpleName, "${listOfEvent.size}")

                        listOfEvent.addPromotedEventsIntoNormalEvents(finalPromotedEventsList)

                        Log.e(ExploreFragment::class.java.simpleName, "${listOfEvent[1]}")
                        Log.e(ExploreFragment::class.java.simpleName, "${listOfEvent[4]}")

                        homeAdapter.notifyDataSetChanged()

                        eventRecycler.visibility = VISIBLE
                        exploreLoadingLayout.visibility = GONE

                    }

                    if (listOfEvent.isEmpty()){

                        exploreEmptyLayout.visibility = VISIBLE
                        exploreLoadingLayout.visibility = GONE
                        exploreEmptyTextView.text = "There are no $eventType events for now"
                    }
                }

            }else{


                eventsViewModel.getEventDocuments().orderBy("eventPostTime", Query.Direction.DESCENDING).limit(6).addSnapshotListener (activity!!) { snapshot3, _ ->

                    if (snapshot3 != null && !snapshot3.isEmpty){

                        if (alreadyLoaded) {
                            lastDocumentSnapshot = snapshot3.documents[(snapshot3.size() - 1)]

                        }

                        for (change in snapshot3.documentChanges) {
                            if (change.type == DocumentChange.Type.ADDED){

                                val eventModel = change.document.breakDocumentIntoEvntsModel()
                                val documentID = change.document.id

                                if (alreadyLoaded){
                                        if (eventModel.eventTags.contains(eventType)){
                                            listOfEvent.add(FullEventsModel(eventModel, documentID))
                                            homeAdapter.notifyDataSetChanged()
                                        }

                                }else{

                                    if (eventModel.eventTags.contains(eventType)){
                                        listOfEvent.add(0, FullEventsModel(eventModel, documentID))
                                        homeAdapter.notifyDataSetChanged()
                                    }
                                }

                            }

                        }

                        alreadyLoaded = false
                    }

                    eventRecycler.visibility = VISIBLE
                    exploreLoadingLayout.visibility = GONE

                    if (listOfEvent.isEmpty()){

                        exploreLoadingLayout.visibility = GONE
                        exploreEmptyLayout.visibility = VISIBLE
                        exploreEmptyTextView.text = "There are no $eventType events for now"
                    }

                }

            }
        }
    }

    private fun loadMoreEvents() {

        eventsViewModel.getEventDocuments().orderBy("eventPostTime", Query.Direction.DESCENDING).limit(3)
            .startAfter(lastDocumentSnapshot).addSnapshotListener(activity!!) { snapshot, _ ->

            if (snapshot != null && !snapshot.isEmpty) {

                lastDocumentSnapshot = snapshot.documents[snapshot.size() - 1]

                for (changes in snapshot.documentChanges){
                    if (changes.type == DocumentChange.Type.ADDED){
                        val eventModel = changes.document.breakDocumentIntoEvntsModel()
                        val eventId = changes.document.id

                        if (!listOfEvent.contains(FullEventsModel(eventModel, eventId))){
                                if (eventModel.eventTags[0] == currentEventType){
                                    listOfEvent.add(FullEventsModel(eventModel, eventId))
                                    homeAdapter.notifyDataSetChanged()
                                }

                        }
                    }
                }

            }
        }
    }
}