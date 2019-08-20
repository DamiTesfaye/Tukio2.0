package io.neolution.eventify.View.Fragments.HomeFragment

import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import io.neolution.eventify.Data.Adapters.HomeAdapter
import io.neolution.eventify.Data.ModelClasses.*
import io.neolution.eventify.Data.ViewModels.EventsViewModel
import io.neolution.eventify.Listeners.OnAddReminderClicked
import io.neolution.eventify.Listeners.OnHomeFragmentsAttached
import io.neolution.eventify.Listeners.OnShareEventClicked
import io.neolution.eventify.R
import io.neolution.eventify.Repos.FireStoreRepo
import io.neolution.eventify.View.Activities.TagsActivity
import io.neolution.eventify.databinding.FragmentHomeBinding
import java.util.*

/**
 * Created by Big-Nosed Developer on the Edge of Infinity.
 */
class HomeFragment: Fragment(){


    lateinit var binding: FragmentHomeBinding
    lateinit var swipeLayout: SwipeRefreshLayout
    lateinit var noInternetLayout: CardView
    lateinit var eventViewModel: EventsViewModel
    private lateinit var onHomeFragmentAttached: OnHomeFragmentsAttached

    private var alreadyLoaded = true
    private lateinit var listOfEvent: MutableList<FullEventsModel>
    private lateinit var lastDocumentSnapshot: DocumentSnapshot

    private lateinit var shareEventListener: OnShareEventClicked
    private lateinit var onAddReminderClicked: OnAddReminderClicked

    private lateinit var adapter: HomeAdapter
    private lateinit var fireStoreRepoInstance: FireStoreRepo

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        fireStoreRepoInstance = FireStoreRepo.getFireStoreRepoInstance()

        swipeLayout = binding.root.findViewById(R.id.frag_home_swipe_layout)
        noInternetLayout = binding.root.findViewById(R.id.frag_home_empty_feed_pic_layout)

        binding.fragHomeChangeInterests.setOnClickListener {
            val intent = Intent(context!!, TagsActivity::class.java)
            intent.putExtra("startedFrom", "")
            startActivity(intent)
        }

        shareEventListener = activity!! as OnShareEventClicked
        onAddReminderClicked = activity!! as OnAddReminderClicked

        listOfEvent = mutableListOf()
        adapter = HomeAdapter(context!!, listOfEvent, activity!!, shareEventListener, onAddReminderClicked)

        binding.fragHomeRecycler.adapter = adapter


        swipeLayout.setOnRefreshListener {
            listOfEvent = mutableListOf()
            loadEvents()
        }


        binding.fragHomeRecycler.run {
            layoutManager = LinearLayoutManager(context!!)
            setHasFixedSize(true)

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                    if (!recyclerView.canScrollVertically(1)) {
                        loadMoreEvents()

                    }

                }

            })
        }
        eventViewModel = ViewModelProviders.of(this).get(EventsViewModel::class.java)

        loadEvents()

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        onHomeFragmentAttached = context as OnHomeFragmentsAttached
        onHomeFragmentAttached.onHomeFragmentAttached()

//        shareEventListener = context!! as OnShareEventClicked
    }

    private var finalPromotedEventsList: MutableList<FullEventsModel>? = null

    private fun loadEvents() {

        alreadyLoaded = true

        binding.fragHomeRecycler.visibility = GONE
        binding.fragHomeFineTuningLayout.visibility = VISIBLE

        eventViewModel.getPromotedEvents().orderBy("eventPostTime", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && !snapshot.isEmpty) {

                    val promotedEventList = mutableListOf<FullEventsModel>()
                    val selectedtPromotedEvents = mutableListOf<FullEventsModel>()


                    for (document in snapshot.documents) {
                        val eventsModel = document.breakDocumentIntoEvntsModel()
                        val id = document.id

                        promotedEventList.add(FullEventsModel(eventsModel, id))
                    }

                    if (promotedEventList.size > 1) {
                        val number1 = Random().nextInt(promotedEventList.size)
                        var number2 = Random().nextInt(promotedEventList.size)

                        while (number2 == number1) {
                            number2 = Random().nextInt(promotedEventList.size)
                        }

                        selectedtPromotedEvents.add(promotedEventList[number1])
                        selectedtPromotedEvents.add(promotedEventList[number2])

                        finalPromotedEventsList = selectedtPromotedEvents.compareAccordingToMoneyPaid()
                    } else {
                        finalPromotedEventsList = promotedEventList
                    }


                    if (activity != null){
                        fireStoreRepoInstance.getCurrentUserDocumentPath()
                            .addSnapshotListener(activity!!) { userSnapshot, _ ->

                                if (userSnapshot != null && userSnapshot.exists()) {
                                    val userModel = userSnapshot.breakDownToUserModel()
                                    val userTags = userModel.tags

                                    if (userTags != null) {

                                        eventViewModel.getEventDocuments()
                                            .orderBy("eventPostTime", Query.Direction.DESCENDING).limit(7)
                                            .addSnapshotListener(activity!!) { querySnapshot, _ ->

                                                if (querySnapshot != null && !querySnapshot.isEmpty) {

                                                    if (alreadyLoaded) {
                                                        lastDocumentSnapshot =
                                                            querySnapshot.documents[querySnapshot.size() - 1]

                                                    }

                                                    for (document in querySnapshot.documentChanges) {

                                                        if (document.type == DocumentChange.Type.ADDED) {
                                                            val eventModel = document.document.breakDocumentIntoEvntsModel()
                                                            val eventID = document.document.id

                                                            if (eventModel.eventTags.compareLists(userTags) > 0) {

                                                                if (!eventModel.eventTitle.startsWith("--beta--", true) && !eventModel.eventTitle.endsWith("--beta--", true)){
                                                                    if (alreadyLoaded) {

                                                                        if (!listOfEvent.contains(
                                                                                FullEventsModel(
                                                                                    eventModel,
                                                                                    eventID
                                                                                )
                                                                            )
                                                                        ) {
                                                                            listOfEvent.add(
                                                                                FullEventsModel(
                                                                                    eventModel,
                                                                                    eventID
                                                                                )
                                                                            )
                                                                            adapter.notifyDataSetChanged()

                                                                        }

                                                                    } else {

                                                                        if (!listOfEvent.contains(
                                                                                FullEventsModel(
                                                                                    eventModel,
                                                                                    eventID
                                                                                )
                                                                            )
                                                                        ) {
                                                                            listOfEvent.add(
                                                                                0,
                                                                                FullEventsModel(eventModel, eventID)
                                                                            )
                                                                            adapter.notifyDataSetChanged()

                                                                        }

                                                                    }
                                                                }

                                                            }

                                                        }
                                                    }

                                                    if (swipeLayout.isRefreshing) {
                                                        swipeLayout.isRefreshing = false
                                                    }

                                                    binding.fragHomeFineTuningLayout.visibility = GONE
                                                    binding.fragHomeRecycler.visibility = VISIBLE


                                                    if (listOfEvent.isEmpty()) {
                                                        loadMoreEvents()


                                                    } else {
                                                        if (finalPromotedEventsList != null) {
                                                            listOfEvent.addPromotedEventsIntoNormalEvents(
                                                                finalPromotedEventsList!!
                                                            )
                                                            adapter.notifyDataSetChanged()
                                                        }

                                                    }

                                                    alreadyLoaded = false

                                                }
                                            }

                                    } else {

                                        noInternetLayout.visibility = VISIBLE
                                        binding.fragHomeFineTuningLayout.visibility = GONE
                                        binding.fragHomeRecycler.visibility = GONE

                                    }

                                }
                            }
                    }

                } else {


                    if (activity != null){
                        fireStoreRepoInstance.getCurrentUserDocumentPath()
                            .addSnapshotListener(activity!!) { userSnapshot, _ ->

                                if (userSnapshot != null && userSnapshot.exists()) {
                                    val userModel = userSnapshot.breakDownToUserModel()
                                    val userTags = userModel.tags

                                    if (userTags != null) {

                                        eventViewModel.getEventDocuments()
                                            .orderBy("eventPostTime", Query.Direction.DESCENDING).limit(7)
                                            .addSnapshotListener(activity!!) { querySnapshot, _ ->

                                                if (querySnapshot != null && !querySnapshot.isEmpty) {

                                                    if (alreadyLoaded) {
                                                        lastDocumentSnapshot =
                                                            querySnapshot.documents[querySnapshot.size() - 1]
                                                    }

                                                    for (document in querySnapshot.documentChanges) {

                                                        if (document.type == DocumentChange.Type.ADDED) {
                                                            val eventModel = document.document.breakDocumentIntoEvntsModel()
                                                            val eventID = document.document.id

                                                            if (!eventModel.eventTitle.startsWith("--beta--", true) && !eventModel.eventTitle.endsWith("--beta--", true)){
                                                                if (eventModel.eventTags.compareLists(userTags) > 0) {

                                                                    if (alreadyLoaded) {

                                                                        if (!listOfEvent.contains(
                                                                                FullEventsModel(
                                                                                    eventModel,
                                                                                    eventID
                                                                                )
                                                                            )
                                                                        ) {
                                                                            listOfEvent.add(
                                                                                FullEventsModel(
                                                                                    eventModel,
                                                                                    eventID
                                                                                )
                                                                            )
                                                                            adapter.notifyDataSetChanged()

                                                                            binding.fragHomeFineTuningLayout.visibility = GONE
                                                                            binding.fragHomeRecycler.visibility = VISIBLE

                                                                        }

                                                                    } else {

                                                                        if (!listOfEvent.contains(
                                                                                FullEventsModel(
                                                                                    eventModel,
                                                                                    eventID
                                                                                )
                                                                            )
                                                                        ) {
                                                                            listOfEvent.add(
                                                                                0,
                                                                                FullEventsModel(eventModel, eventID)
                                                                            )
                                                                            adapter.notifyDataSetChanged()

                                                                        }

                                                                    }

                                                                }
                                                            }
                                                        }

                                                    }

                                                    if (swipeLayout.isRefreshing) {
                                                        swipeLayout.isRefreshing = false
                                                    }




                                                    if (listOfEvent.isEmpty()) {
                                                        binding.fragHomeEmptyFeedPicLayout.visibility = VISIBLE
                                                        binding.fragHomeFineTuningLayout.visibility = GONE
                                                        binding.fragHomeRecycler.visibility = GONE


                                                    } else {
                                                        if (finalPromotedEventsList != null) {
                                                            listOfEvent.addPromotedEventsIntoNormalEvents(
                                                                finalPromotedEventsList!!
                                                            )
                                                            adapter.notifyDataSetChanged()
                                                        }

                                                    }

                                                    alreadyLoaded = false

                                                }
                                            }

                                    } else {

                                        noInternetLayout.visibility = VISIBLE
                                        binding.fragHomeFineTuningLayout.visibility = GONE
                                        binding.fragHomeRecycler.visibility = GONE

                                    }
                                }

                            }
                    }
                }
            }
    }


    private fun loadMoreEvents() {

        fireStoreRepoInstance.getCurrentUserDocumentPath().addSnapshotListener(activity!!) { userSnapshot, _ ->

            if (userSnapshot != null && userSnapshot.exists()) {
                val userModel = userSnapshot.breakDownToUserModel()
                val usersTags = userModel.tags


                if (usersTags != null) {

                    eventViewModel.getEventDocuments().orderBy("eventPostTime", Query.Direction.DESCENDING)
                        .limit(5).startAfter(lastDocumentSnapshot).addSnapshotListener(activity!!) { snapshot, _ ->

                            if (snapshot != null && !snapshot.isEmpty) {

                                lastDocumentSnapshot = snapshot.documents[snapshot.size() - 1]

                                for (document in snapshot.documents) {
                                    val eventModel = document.breakDocumentIntoEvntsModel()
                                    if (!eventModel.eventTitle.startsWith("--beta--", true) && !eventModel.eventTitle.endsWith("--beta--", true)){
                                        if (usersTags.compareLists(eventModel.eventTags) > 0) {
                                            if (!listOfEvent.contains(FullEventsModel(eventModel, document.id))) {
                                                listOfEvent.add(FullEventsModel(eventModel, document.id))

                                                adapter.notifyDataSetChanged()

                                            }
                                        }
                                    }

                                }

                            }

                        }
                }
            }


            if (listOfEvent.isEmpty()) {
                binding.fragHomeFineTuningLayout.visibility = GONE
                binding.fragHomeRecycler.visibility = GONE
                binding.fragHomeEmptyFeedPicLayout.visibility = VISIBLE
            } else if (listOfEvent.isNotEmpty() && listOfEvent.size <= 5) {

                if (finalPromotedEventsList != null) {
                    listOfEvent.addPromotedEventsIntoNormalEvents(finalPromotedEventsList!!)
                }

            }

        }

    }

}