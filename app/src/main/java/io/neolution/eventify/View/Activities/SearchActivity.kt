package io.neolution.eventify.View.Activities

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import io.neolution.eventify.Data.Adapters.HomeAdapter
import io.neolution.eventify.Data.ModelClasses.*
import io.neolution.eventify.Data.ViewModels.EventsViewModel
import io.neolution.eventify.Listeners.OnAddReminderClicked
import io.neolution.eventify.Listeners.OnShareEventClicked
import io.neolution.eventify.R
import io.neolution.eventify.Repos.FireStoreRepo
import io.neolution.eventify.View.Fragments.HomeFragment.ExploreFragment
import java.util.*

class SearchActivity : AppCompatActivity(), OnAddReminderClicked, OnShareEventClicked {

    override fun onShareButtonClick(eventTitle: String, eventId: String, eventLocation: String, eventDate: String) {

    }

    override fun OnAddReminderButtonClicked(timeInMillis: Long) {

    }

    private lateinit var backBtn: ImageButton
    private lateinit var searchEditText: EditText
    private lateinit var searchBtn: ImageButton

    private lateinit var searchRecycler: RecyclerView
    private lateinit var searchLayout: LinearLayout
    private lateinit var searchingLayout: LinearLayout
    private lateinit var emptyLayout: LinearLayout

    private var canBeSubmitted = false
    private var alreadyLoaded = true

    lateinit var eventViewModel: EventsViewModel
    private lateinit var adapter: HomeAdapter
    private lateinit var fireStoreRepoInstance: FireStoreRepo
    private lateinit var lastDocumentSnapshot: DocumentSnapshot
    private lateinit var listOfEvent: MutableList<FullEventsModel>

    private var currentSearchType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        backBtn = findViewById(R.id.search_activity_back_btn)
        searchEditText = findViewById(R.id.search_activity_edit_text)
        searchBtn = findViewById(R.id.search_activity_search_btn)
        searchBtn.isEnabled = false

        eventViewModel = ViewModelProviders.of(this).get(EventsViewModel::class.java)
        fireStoreRepoInstance = FireStoreRepo()

        searchRecycler = findViewById(R.id.search_activity_recycler)
        searchLayout = findViewById(R.id.search_activity_search_placeholder)
        searchingLayout = findViewById(R.id.search_activity_searchind_layout)
        emptyLayout = findViewById(R.id.search_activity_empty_search_layout)

        searchRecycler.setHasFixedSize(true)
        searchRecycler.layoutManager = LinearLayoutManager(this)

        listOfEvent = mutableListOf()
        adapter = HomeAdapter(context = this, activity = this, listOfEvents = listOfEvent, onAddReminderClicked = this, shareEventListener = this)

        searchRecycler.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (!recyclerView.canScrollVertically(1)) {
                    loadMoreEvents()

                }
            }
        })

        backBtn.setOnClickListener {
            finish()
        }

        searchEditText.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean{

                if (canBeSubmitted){
                    if (p0 != null){
                        if (p1 == EditorInfo.IME_ACTION_DONE && p2!!.action == KeyEvent.ACTION_DOWN){
                            closeKeyboard()
                            val searchText = p0.text.toString()
                            searchEvents(searchText)
                        }

                    }
                }

                return true
            }
        })

        searchBtn.setOnClickListener {
            if (canBeSubmitted){
                closeKeyboard()
                val searchText = searchEditText.text.toString().trim()
                searchEvents(searchText)
            }
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0 != null){
                    if (p0.toString().trim().isNotEmpty()){
                        canBeSubmitted = true
                        searchBtn.isEnabled = true
                    }else{
                        canBeSubmitted = false
                        searchBtn.isEnabled = false
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
    }

    private fun searchEvents(searchString: String){

        currentSearchType = searchString

        listOfEvent.clear()
        adapter.notifyDataSetChanged()

        searchLayout.visibility = GONE
        searchingLayout.visibility = VISIBLE
        searchRecycler.visibility = GONE
        emptyLayout.visibility = GONE

        var finalPromotedEventsList: MutableList<FullEventsModel>
        searchRecycler.adapter = adapter

        eventViewModel.getPromotedEvents().orderBy("eventPostTime", Query.Direction.DESCENDING)
            .addSnapshotListener(this) { snapshot, _ ->
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


                    eventViewModel.getEventDocuments().orderBy("eventPostTime", Query.Direction.DESCENDING).limit(6).addSnapshotListener (this) { snapshot2, _ ->

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
                                            if (eventModel.eventTitle.toLowerCase().contains(searchString)){
                                                listOfEvent.add(FullEventsModel(eventModel, documentID))
                                                adapter.notifyDataSetChanged()
                                            }
                                        }

                                    }else{

                                        if (!listOfEvent.contains(FullEventsModel(eventModel, documentID))){
                                            if (eventModel.eventTitle.toLowerCase().contains(searchString)){
                                                listOfEvent.add(0, FullEventsModel(eventModel, documentID))
                                                adapter.notifyDataSetChanged()
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

                            adapter.notifyDataSetChanged()

                            searchRecycler.visibility = VISIBLE
                            searchingLayout.visibility = GONE

                        }

                        if (listOfEvent.isEmpty()){

                            searchRecycler.visibility = GONE
                            searchingLayout.visibility = GONE
                            emptyLayout.visibility = VISIBLE
                        }
                    }

                } else {

                    eventViewModel.getEventDocuments().orderBy("eventPostTime", Query.Direction.DESCENDING).limit(6).addSnapshotListener (this) { snapshot2, _ ->

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
                                            if (eventModel.eventTitle.toLowerCase().contains(searchString.toLowerCase())){
                                                listOfEvent.add(FullEventsModel(eventModel, documentID))
                                                adapter.notifyDataSetChanged()
                                            }
                                        }

                                    }else{

                                        if (!listOfEvent.contains(FullEventsModel(eventModel, documentID))){
                                            if (eventModel.eventTitle.toLowerCase().contains(searchString.toLowerCase())){
                                                listOfEvent.add(0, FullEventsModel(eventModel, documentID))
                                                adapter.notifyDataSetChanged()
                                            }
                                        }
                                    }
                                }
                            }
                            alreadyLoaded = false
                        }

                        if (!listOfEvent.isEmpty()){
                            adapter.notifyDataSetChanged()

                            searchRecycler.visibility = VISIBLE
                            searchingLayout.visibility = GONE
                        }

                        if (listOfEvent.isEmpty()){

                            searchRecycler.visibility = GONE
                            searchingLayout.visibility = GONE
                            emptyLayout.visibility = VISIBLE
                        }
                    }
                }
            }
    }

    private fun loadMoreEvents() {
        if (currentSearchType != null){
            eventViewModel.getEventDocuments().orderBy("eventPostTime", Query.Direction.DESCENDING).limit(3)
                .startAfter(lastDocumentSnapshot).addSnapshotListener(this) { snapshot, _ ->

                    if (snapshot != null && !snapshot.isEmpty) {

                        lastDocumentSnapshot = snapshot.documents[snapshot.size() - 1]

                        for (changes in snapshot.documentChanges){
                            if (changes.type == DocumentChange.Type.ADDED){
                                val eventModel = changes.document.breakDocumentIntoEvntsModel()
                                val eventId = changes.document.id

                                if (!listOfEvent.contains(FullEventsModel(eventModel, eventId))){
                                    if (eventModel.eventTitle.toLowerCase().contains(currentSearchType!!.toLowerCase())){
                                        listOfEvent.add(FullEventsModel(eventModel, eventId))
                                        adapter.notifyDataSetChanged()
                                    }
                                }
                            }
                        }

                    }
                }
        }

    }

    override fun onBackPressed() {
        finish()
    }

    private fun closeKeyboard(){
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = currentFocus
        if (view == null){
           view = View(this)
        }

        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}