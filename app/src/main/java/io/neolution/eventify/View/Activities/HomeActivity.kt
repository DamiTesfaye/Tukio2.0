package io.neolution.eventify.View.Activities

import android.app.ActivityOptions
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View.GONE
import android.widget.ImageButton
import android.widget.LinearLayout
import com.google.firebase.messaging.FirebaseMessaging
import io.neolution.eventify.Data.Adapters.HomePagerAdapter
import io.neolution.eventify.Data.ModelClasses.breakDownToUserModel
import io.neolution.eventify.Data.ModelClasses.indicate
import io.neolution.eventify.Listeners.OnHomeFragmentsAttached
import io.neolution.eventify.Listeners.OnShareEventClicked
import io.neolution.eventify.R
import io.neolution.eventify.Repos.AuthRepo
import io.neolution.eventify.Repos.FireStoreRepo
import io.neolution.eventify.Utils.FirebaseUtils
import io.neolution.eventify.Utils.IntentUtils
import io.neolution.eventify.View.CustomViews.CustomViewPager
import io.neolution.eventify.View.Fragments.HomeFragment.ExploreFragment
import io.neolution.eventify.View.Fragments.HomeFragment.HomeFragment
import io.neolution.eventify.View.Fragments.HomeFragment.UpdatesFragment
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(),  OnHomeFragmentsAttached, OnShareEventClicked {

    override fun onShareButtonClick(eventTitle: String, eventId: String, eventLocation: String, eventDate: String) {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        eventTitleToBeShared = eventTitle
        eventDateToBeShared = eventDate
        eventLocationToBeShared = eventLocation
        eventIDToBeShared = eventId
    }

    override fun onHomeFragmentAttached() {

        homeFab.apply {
            setOnClickListener {

                startActivity(Intent(this@HomeActivity, AddEventPremActivity::class.java))
            }
            setImageDrawable(resources.getDrawable(R.drawable.ic_add_black_24dp))
        }
    }

    override fun onExploreFragmentAttached() {

//        homeFab.visibility = GONE
    }

    override fun onUpdateFragmentAttached() {
        supportActionBar?.show()
    }

    lateinit var adapter: HomePagerAdapter
    lateinit var broadcast: InternetBroadcast
    lateinit var filter: IntentFilter
    lateinit var homeFab: android.support.design.widget.FloatingActionButton
    private lateinit var shareEventBottomSheet: LinearLayout
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private lateinit var eventTitleToBeShared: String
    private lateinit var eventIDToBeShared: String
    private lateinit var eventDateToBeShared: String
    private lateinit var eventLocationToBeShared: String

    private lateinit var closeShareEventBottomSheetBtn: ImageButton
    private lateinit var shareBsheetTukPicBtn: LinearLayout
    private lateinit var shareBsheetSocialMedia: LinearLayout
    private lateinit var shareBsheetCopyLink: LinearLayout

    var isConnected = false
    var registered = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        checkIfUserExits()

        homeFab = findViewById(R.id.home_fab)
        shareEventBottomSheet = findViewById(R.id.share_event_bottomsheet)
        bottomSheetBehavior = BottomSheetBehavior.from(shareEventBottomSheet)

        closeShareEventBottomSheetBtn = findViewById(R.id.share_bsheet_close)
        closeShareEventBottomSheetBtn.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        shareBsheetTukPicBtn = findViewById(R.id.share_bsheet_tukpic)
        shareBsheetTukPicBtn.setOnClickListener {
            //TODO: SHOULD TAKE USER TO TUKPIC CREATING ACTIVITY
            //FOR NOW,
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        shareBsheetSocialMedia = findViewById(R.id.share_bsheet_socialmedia)
        shareBsheetSocialMedia.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            IntentUtils.shareEvent(context = this, eventDate = eventDateToBeShared, eventLocation = eventLocationToBeShared,
                eventTitle = eventTitleToBeShared, eventID = eventIDToBeShared)
        }

        shareBsheetCopyLink = findViewById(R.id.share_bsheet_copylink)

        val currentUserId = AuthRepo.getUserUid()
        updateUid(currentUserId)

        broadcast = InternetBroadcast()

        filter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(broadcast, filter)

        FireStoreRepo().getCurrentUserDocumentPath().addSnapshotListener(this) {
            snapshot, _ ->
            if (snapshot != null && snapshot.exists()){
                val userModel = snapshot.breakDownToUserModel()
                FirebaseUtils().saveUserDetails(userModel, this@HomeActivity)

            }
        }

        val fragmentList = listOf(
            HomeFragment(),
            ExploreFragment(),
            UpdatesFragment()
        )

        FirebaseMessaging.getInstance().subscribeToTopic("Owner")
            .addOnCompleteListener { task ->

                if (!task.isSuccessful) {
                    Log.e(HomeActivity::class.java.simpleName, "Subscribed Unsuccessfully!")
                }else{
                    Log.e(HomeActivity::class.java.simpleName, "Subscribed Successfully!")
                }

            }

        adapter = HomePagerAdapter(supportFragmentManager, fragmentList)
        val viewPager: CustomViewPager = findViewById(R.id.home_view_pager)
        viewPager.adapter = adapter

        homeFab.setOnClickListener {
            startActivity(Intent(this@HomeActivity, AddEventPremActivity::class.java))
        }


        home_bottom_nav_bar.setOnNavigationItemSelectedListener {
            item ->
            when (item.itemId){
                R.id.main_menu_home -> {
                    viewPager.setCurrentItem(0,false)

                    return@setOnNavigationItemSelectedListener true
                }

                R.id.main_menu_explore -> {
                    viewPager.setCurrentItem(1,false)
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.main_menu_update -> {
                    viewPager.setCurrentItem(2,false)
                    return@setOnNavigationItemSelectedListener true
                }

                else -> {
                    return@setOnNavigationItemSelectedListener true
                }

            }
        }

    }

    override fun onPause() {
        super.onPause()

        checkRecieverUnRegister(broadcast)
    }

    override fun onDestroy() {
        super.onDestroy()

        checkRecieverUnRegister(broadcast)
    }

    private fun checkRecieverUnRegister(broadcast: BroadcastReceiver) {
        if (!registered) {
            unregisterReceiver(broadcast)
            registered = true
        }
    }

    private fun checkIfUserExits(){
        if (AuthRepo.getCurrentUser() == null){
            startActivity(Intent(this, AuthActivity::class.java))
        }
    }

    fun updateUid(uId: String){
        FirebaseUtils().saveUserUID(uId, this)
    }

    inner class InternetBroadcast : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if (manager != null) {
                val info = manager.activeNetworkInfo

                if (info != null) {
                    if (info.isConnected) {

                        indicate("You're connected")

                    }
                } else {

                    indicate("You're not connected")

                }


            }

        }
    }
}


