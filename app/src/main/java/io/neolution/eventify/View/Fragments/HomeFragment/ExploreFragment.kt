package io.neolution.eventify.View.Fragments.HomeFragment

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import io.neolution.eventify.Data.Adapters.EventTypeAdapter
import io.neolution.eventify.Data.Adapters.HomeAdapter
import io.neolution.eventify.Data.ModelClasses.*
import io.neolution.eventify.Data.ViewModels.EventsViewModel
import io.neolution.eventify.Listeners.OnHomeFragmentsAttached
import io.neolution.eventify.R
import io.neolution.eventify.databinding.NewExploreLayoutBinding
import java.util.*


/**
 * Created by Big-Nosed Developer on the Edge of Infinity.
 */
class ExploreFragment: Fragment(){

    lateinit var binding: NewExploreLayoutBinding
    lateinit var eventsViewModel: EventsViewModel
    lateinit var swipeLayout: SwipeRefreshLayout

    private lateinit var onHomeFragmentsAttached: OnHomeFragmentsAttached

    lateinit var adapter: HomeAdapter
    lateinit var listOfEvent : MutableList<FullEventsModel>
    lateinit var lastDocumentSnapshot: DocumentSnapshot
    private var alreadyLoaded = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        //Initializing
        binding = DataBindingUtil.inflate(inflater, R.layout.new_explore_layout, container!!, false)
        val recyclerView = binding.root.findViewById<RecyclerView>(R.id.new_explore_tag_recycler)
        recyclerView.setHasFixedSize(true)

        val chipList = mutableListOf(
            ChipModel("Tech", R.drawable.ic_robot),
            ChipModel("Movement", R.drawable.ic_robot)
            , ChipModel("MeetUp", R.drawable.ic_robot),
            ChipModel("Business", R.drawable.ic_robot),
            ChipModel("Concerts", R.drawable.ic_stage)
            , ChipModel("Conventions/Conferences", R.drawable.ic_robot),
            ChipModel("Parties", R.drawable.ic_birthday_cake),
            ChipModel("Workshops/Seminars", R.drawable.ic_robot),
            ChipModel("Birthdays", R.drawable.ic_birthday_cake),
            ChipModel("Christian", R.drawable.ic_church),
            ChipModel("Muslim", R.drawable.ic_mosque),
            ChipModel("Football", R.drawable.ic_soccer),
            ChipModel("Basketball", R.drawable.ic_basketball),
            ChipModel("Tests", R.drawable.ic_school),
            ChipModel("Examination", R.drawable.ic_school),
            ChipModel("Movies", R.drawable.ic_popcorn)
        )

        val adapter = EventTypeAdapter(chipList, context!!)
        val manager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL)
        manager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE

        recyclerView.layoutManager = manager
        recyclerView.adapter = adapter
//        binding.eventTypesRecycler.setHasFixedSize(true)

//
//        binding.eventTypesRecycler.layoutManager = manager
//        binding.eventTypesRecycler.adapter = adapter
//        eventsViewModel = ViewModelProviders.of(this).get(EventsViewModel::class.java)
//
//        binding.fragExploreRecycler.apply {
//            layoutManager = LinearLayoutManager(context!!)
//            setHasFixedSize(true)
//        }
//
//        listOfEvent = mutableListOf()
//        adapter = HomeAdapter(context!!, listOfEvent, activity!!)
//
//        binding.fragExploreRecycler.adapter = adapter
//
//        binding.fragExploreRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//
//            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
//
//                if (!recyclerView!!.canScrollVertically(1)){
//                    loadMoreEvents()
//
//                }
//
//            }
//        })
//
//         swipeLayout = binding.root.findViewById(R.id.frag_explore_swipe_layout)
//        swipeLayout.setOnRefreshListener {
//
//            listOfEvent = mutableListOf()
//            loadExplore()
//
//        }
//
//        loadExplore()

        return binding.root
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        onHomeFragmentsAttached = context!! as OnHomeFragmentsAttached
        onHomeFragmentsAttached.onExploreFragmentAttached()
    }

//    private fun loadMoreEvents() {
//
//        eventsViewModel.getEventDocuments().orderBy("eventPostTime", Query.Direction.DESCENDING).limit(3)
//            .startAfter(lastDocumentSnapshot).addSnapshotListener(activity!!) { snapshot, _ ->
//
//            if (snapshot != null && !snapshot.isEmpty) {
//
//                lastDocumentSnapshot = snapshot.documents[snapshot.size() - 1]
//
//                for (changes in snapshot.documentChanges){
//                    if (changes.type == DocumentChange.Type.ADDED){
//                        val eventModel = changes.document.breakDocumentIntoEvntsModel()
//                        val eventId = changes.document.id
//
//                        if (!listOfEvent.contains(FullEventsModel(eventModel, eventId))){
//                            listOfEvent.add(FullEventsModel(eventModel, eventId))
//                            adapter.notifyDataSetChanged()
//                        }
//
//                    }
//                }
//
//            }
//        }
//
//    }

//    private fun loadExplore() {
//
//        binding.fragExploreLoading.visibility = VISIBLE
//        binding.fragExploreRecycler.visibility = GONE
//
//        alreadyLoaded = true
//
//        eventsViewModel.getPromotedEvents().orderBy("eventPostTime", Query.Direction.DESCENDING).addSnapshotListener(activity!!) { snapshot, _ ->
//
//            if (snapshot != null && !snapshot.isEmpty) {
//
//                val promotedEventList = mutableListOf<FullEventsModel>()
//                val selectedtPromotedEvents = mutableListOf<FullEventsModel>()
//                val finalPromotedEventsList: List<FullEventsModel>
//
//                for (document in snapshot.documents){
//                    val eventsModel = document.breakDocumentIntoEvntsModel()
//                    val id = document.id
//
//                    promotedEventList.add(FullEventsModel(eventsModel, id))
//                }
//
//                if (promotedEventList.size > 1){
//                    val number1 = Random().nextInt(promotedEventList.size)
//                    var number2 = Random().nextInt(promotedEventList.size)
//
//                    while (number2 == number1){
//                        number2 = Random().nextInt(promotedEventList.size)
//                    }
//
//                    selectedtPromotedEvents.add(promotedEventList[number1])
//                    selectedtPromotedEvents.add(promotedEventList[number2])
//
//                    finalPromotedEventsList = selectedtPromotedEvents.compareAccordingToMoneyPaid()
//                    Log.e(ExploreFragment::class.java.simpleName, finalPromotedEventsList.toString())
//                }else{
//                    finalPromotedEventsList = promotedEventList
//                }
//
//                eventsViewModel.getEventDocuments().orderBy("eventPostTime", Query.Direction.DESCENDING).limit(6).addSnapshotListener (activity!!) { snapshot2, _ ->
//
//                    if (snapshot2 != null && !snapshot2.isEmpty){
//
//                        if (alreadyLoaded) {
//                            lastDocumentSnapshot = snapshot2.documents[(snapshot2.size() - 1)]
//
//                        }
//
//                        for (change in snapshot2.documentChanges) {
//                            if (change.type == DocumentChange.Type.ADDED){
//
//                                val eventModel = change.document.breakDocumentIntoEvntsModel()
//                                val documentID = change.document.id
//
//
//                                if (alreadyLoaded){
//                                    if (!listOfEvent.contains(FullEventsModel(eventModel, documentID))){
//                                        listOfEvent.add(FullEventsModel(eventModel, documentID))
//                                        adapter.notifyDataSetChanged()
//                                    }
//
//                                }else{
//
//                                    if (!listOfEvent.contains(FullEventsModel(eventModel, documentID))){
//                                        listOfEvent.add(0, FullEventsModel(eventModel, documentID))
//                                        adapter.notifyDataSetChanged()
//                                    }
//
//                                }
//
//                            }
//
//                        }
//                    }
//
//                    alreadyLoaded = false
//
//                    if (!listOfEvent.isEmpty()){
//
//
//                        Log.e(ExploreFragment::class.java.simpleName, "${listOfEvent.size}")
//
//                        listOfEvent.addPromotedEventsIntoNormalEvents(finalPromotedEventsList)
//
//                        Log.e(ExploreFragment::class.java.simpleName, "${listOfEvent[1]}")
//                        Log.e(ExploreFragment::class.java.simpleName, "${listOfEvent[4]}")
//
//                        adapter.notifyDataSetChanged()
//
//                        binding.fragExploreLoading.visibility = GONE
//                        binding.fragExploreRecycler.visibility = VISIBLE
//
//                    }
//
//                    if (listOfEvent.isEmpty()){
//                        binding.fragExploreNoInternet.visibility = VISIBLE
//                        binding.fragExploreLoading.visibility = GONE
//                        binding.fragExploreRecycler.visibility = GONE
//                    }
//
//
//
//                    if (swipeLayout.isRefreshing) swipeLayout.isRefreshing = false
//
//                }
//
//
//
//
//
//            }else{
//
//                binding.fragExploreLoading.visibility = VISIBLE
//                binding.fragExploreRecycler.visibility = GONE
//
//                alreadyLoaded = true
//
//                eventsViewModel.getEventDocuments().orderBy("eventPostTime", Query.Direction.DESCENDING).limit(6).addSnapshotListener (activity!!) { snapshot3, _ ->
//
//                    if (snapshot3 != null && !snapshot3.isEmpty){
//
//                        if (alreadyLoaded) {
//                            lastDocumentSnapshot = snapshot3.documents[(snapshot3.size() - 1)]
//
//                        }
//
//                        for (change in snapshot3.documentChanges) {
//                            if (change.type == DocumentChange.Type.ADDED){
//
//                                val eventModel = change.document.breakDocumentIntoEvntsModel()
//                                val documentID = change.document.id
//
//                                if (alreadyLoaded){
//
//                                    listOfEvent.add(FullEventsModel(eventModel, documentID))
//                                    adapter.notifyDataSetChanged()
//
//                                }else{
//
//                                    listOfEvent.add(0, FullEventsModel(eventModel, documentID))
//                                    adapter.notifyDataSetChanged()
//                                }
//
//                            }
//
//                        }
//
//                        alreadyLoaded = false
//
//                    }
//
//
//
//
//
//                    binding.fragExploreLoading.visibility = GONE
//                    binding.fragExploreRecycler.visibility = VISIBLE
//
//                    if (listOfEvent.isEmpty()){
//
//                        binding.fragExploreNoInternet.visibility = VISIBLE
//                        binding.fragExploreLoading.visibility = GONE
//                        binding.fragExploreRecycler.visibility = GONE
//
//                    }
//
//                    if (swipeLayout.isRefreshing) swipeLayout.isRefreshing = false
//
//                }
//
//            }
//        }
//
//
//    }
}