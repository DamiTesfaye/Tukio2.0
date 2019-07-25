package io.neolution.eventify.View.Activities

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View.VISIBLE
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import io.neolution.eventify.Data.Adapters.HomeAdapter
import io.neolution.eventify.Data.ModelClasses.FullEventsModel
import io.neolution.eventify.Data.ModelClasses.addPromotedEventsIntoNormalEvents
import io.neolution.eventify.Data.ModelClasses.breakDocumentIntoEvntsModel
import io.neolution.eventify.Data.ModelClasses.compareAccordingToMoneyPaid
import io.neolution.eventify.Data.ViewModels.EventsViewModel
import io.neolution.eventify.Listeners.OnShareEventClicked
import io.neolution.eventify.R
import io.neolution.eventify.Repos.FireStoreRepo
import io.neolution.eventify.Utils.AppUtils
import io.neolution.eventify.View.Fragments.HomeFragment.ExploreFragment
import kotlinx.android.synthetic.main.activity_tag.*
import java.util.*

class TagActivity : AppCompatActivity(), OnShareEventClicked {

    override fun onShareButtonClick(eventTitle: String, eventId: String, eventLocation: String, eventDate: String) {
    }

    private lateinit var adapter: HomeAdapter
    private lateinit var listOfEvents: MutableList<FullEventsModel>
    private var alreadyLoaded: Boolean = true
    lateinit var eventViewModel: EventsViewModel
    private lateinit var title__: String

    private lateinit var fireStoreRepoInstance: FireStoreRepo
    private var finalPromotedEventsList: MutableList<FullEventsModel>? = null
    private lateinit var lastDocumentSnapshot: DocumentSnapshot

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tag)

        fireStoreRepoInstance = FireStoreRepo.getFireStoreRepoInstance()
        tag_activity_close.setOnClickListener {
            onBackPressed()
        }

        listOfEvents = mutableListOf()
        adapter = HomeAdapter(this, listOfEvents, this, this)
        eventViewModel = ViewModelProviders.of(this).get(EventsViewModel::class.java)

        tag_activity_recycler.run{
            layoutManager = AppUtils.getRecycleLayout(AppUtils.LINEAR_LAYOUT_MANAGER, this@TagActivity)
        }

        tag_activity_recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {

                if (!recyclerView!!.canScrollVertically(1)){
                    loadMoreEvents(title__)

                }

            }

        })

        tag_activity_recycler.adapter = adapter

        title__ = intent.getStringExtra("title")
        if (!title__.isNullOrEmpty()){
            if (title__.length > 15){
                val finalTitle =  "${title__.substring(0, 15)}... events"
                tag_activity_title.text = finalTitle
                loadEvents(title__)
            }else{
                val finalTitle = "$title__ events.."
                tag_activity_title.text = finalTitle
                loadEvents(title__)
            }

        }




    }

    private fun loadEvents(s: String?) {
        tag_activity_progress_bar.visibility = VISIBLE

        alreadyLoaded = true


        eventViewModel.getPromotedEvents().orderBy("eventPostTime", Query.Direction.DESCENDING).addSnapshotListener(this) { snapshot, _ ->

            if (snapshot != null && !snapshot.isEmpty) {

                val promotedEventList = mutableListOf<FullEventsModel>()
                val selectedtPromotedEvents = mutableListOf<FullEventsModel>()
                val finalPromotedEventsList: List<FullEventsModel>

                for (document in snapshot.documents){
                    val eventsModel = document.breakDocumentIntoEvntsModel()
                    val id = document.id

                    promotedEventList.add(FullEventsModel(eventsModel, id))
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

                eventViewModel.getEventDocuments().orderBy("eventPostTime", Query.Direction.DESCENDING).limit(6).addSnapshotListener (this) { snapshot2, _ ->

                    if (snapshot2 != null && !snapshot2.isEmpty){

                        if (alreadyLoaded) {
                            lastDocumentSnapshot = snapshot2.documents[(snapshot2.size() - 1)]

                        }

                        for (change in snapshot2.documentChanges) {
                            if (change.type == DocumentChange.Type.ADDED){

                                val eventModel = change.document.breakDocumentIntoEvntsModel()
                                val documentID = change.document.id

                                if (eventModel.eventTags.contains(s)){
                                    if (alreadyLoaded){
                                        if (!listOfEvents.contains(FullEventsModel(eventModel, documentID))){
                                            listOfEvents.add(FullEventsModel(eventModel, documentID))
                                            adapter.notifyDataSetChanged()
                                        }

                                    }else{

                                        if (!listOfEvents.contains(FullEventsModel(eventModel, documentID))){
                                            listOfEvents.add(0, FullEventsModel(eventModel, documentID))
                                            adapter.notifyDataSetChanged()
                                        }

                                    }
                                }

                            }
                        }
                    }

                    alreadyLoaded = false

                    if (!listOfEvents.isEmpty()){

                        listOfEvents.addPromotedEventsIntoNormalEvents(finalPromotedEventsList)
                        adapter.notifyDataSetChanged()

//                        binding.fragExploreLoading.visibility = GONE
//                        binding.fragExploreRecycler.visibility = VISIBLE

                    }

                    if (listOfEvents.isEmpty()){
//                        binding.fragExploreNoInternet.visibility = VISIBLE
//                        binding.fragExploreLoading.visibility = GONE
//                        binding.fragExploreRecycler.visibility = GONE
                    }



//                    if (swipeLayout.isRefreshing) swipeLayout.isRefreshing = false

                }





            }else{

//                binding.fragExploreLoading.visibility = VISIBLE
//                binding.fragExploreRecycler.visibility = GONE

                alreadyLoaded = true

                eventViewModel.getEventDocuments().orderBy("eventPostTime", Query.Direction.DESCENDING).limit(6).addSnapshotListener (this) { snapshot3, _ ->

                    if (snapshot3 != null && !snapshot3.isEmpty){

                        if (alreadyLoaded) {
                            lastDocumentSnapshot = snapshot3.documents[(snapshot3.size() - 1)]

                        }

                        for (change in snapshot3.documentChanges) {
                            if (change.type == DocumentChange.Type.ADDED){

                                val eventModel = change.document.breakDocumentIntoEvntsModel()
                                val documentID = change.document.id

                                if (alreadyLoaded){

                                    listOfEvents.add(FullEventsModel(eventModel, documentID))
                                    adapter.notifyDataSetChanged()

                                }else{

                                    listOfEvents.add(0, FullEventsModel(eventModel, documentID))
                                    adapter.notifyDataSetChanged()
                                }

                            }

                        }

                        alreadyLoaded = false

                    }





//                    binding.fragExploreLoading.visibility = GONE
//                    binding.fragExploreRecycler.visibility = VISIBLE

                    if (listOfEvents.isEmpty()){

//                        binding.fragExploreNoInternet.visibility = VISIBLE
//                        binding.fragExploreLoading.visibility = GONE
//                        binding.fragExploreRecycler.visibility = GONE

                    }

//                    if (swipeLayout.isRefreshing) swipeLayout.isRefreshing = false

                }

            }
        }
    }


    private fun loadMoreEvents(s: String) {


        eventViewModel.getEventDocuments().orderBy("eventPostTime", Query.Direction.DESCENDING)
            .limit(5).startAfter(lastDocumentSnapshot).addSnapshotListener(this) { snapshot, _ ->

                if (snapshot != null && !snapshot.isEmpty) {

                    lastDocumentSnapshot = snapshot.documents[snapshot.size() - 1]

                    for (document in snapshot.documents) {
                        val eventModel = document.breakDocumentIntoEvntsModel()

                            if (eventModel.eventTags.contains(s)){
                                if (!listOfEvents.contains(FullEventsModel(eventModel, document.id))) {
                                    listOfEvents.add(FullEventsModel(eventModel, document.id))

                                    adapter.notifyDataSetChanged()

                                }
                            }



                    }

                }
            }


    }
}
