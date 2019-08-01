package io.neolution.eventify.View.Activities

import android.app.ActivityOptions
import android.content.*
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessaging
import io.neolution.eventify.Data.Adapters.HomePagerAdapter
import io.neolution.eventify.Data.ModelClasses.breakDownToUserModel
import io.neolution.eventify.Data.ModelClasses.indicate
import io.neolution.eventify.Listeners.OnAddReminderClicked
import io.neolution.eventify.Listeners.OnEditProfileClicked
import io.neolution.eventify.Listeners.OnHomeFragmentsAttached
import io.neolution.eventify.Listeners.OnShareEventClicked
import io.neolution.eventify.R
import io.neolution.eventify.Repos.AuthRepo
import io.neolution.eventify.Repos.FireStoreRepo
import io.neolution.eventify.Utils.FirebaseUtils
import io.neolution.eventify.Utils.IntentUtils
import io.neolution.eventify.View.Fragments.HomeFragment.ExploreFragment
import io.neolution.eventify.View.Fragments.HomeFragment.HomeFragment
import io.neolution.eventify.View.Fragments.HomeFragment.Profile
import io.neolution.eventify.View.Fragments.HomeFragment.UpdatesFragment
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : AppCompatActivity(),  OnHomeFragmentsAttached, OnShareEventClicked, OnAddReminderClicked, OnEditProfileClicked {

    override fun onEditButtonClicked() {
        profileBottomSheetBehaviour.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onTrendsFragmentAttached() {

    }

    override fun onProfileFragmentAttached() {

    }

    override fun OnAddReminderButtonClicked(timeInMillis: Long) {

        var calName: String = ""
        var calId: String = ""

        val projection = arrayOf("_id", "name")
        val calendars = Uri.parse("content://calendar/calendars")

        val managedCursor = managedQuery(
            calendars, projection,
            "selected=1", null, null
        )

        if (managedCursor.moveToFirst()) {

            val nameColumn = managedCursor.getColumnIndex("name")
            val idColumn = managedCursor.getColumnIndex("_id")
            do {
                calName = managedCursor.getString(nameColumn)
                calId = managedCursor.getString(idColumn)
            } while (managedCursor.moveToNext())
        }

        val event = ContentValues()
        event.put("calendar_id", calId)
        event.put("title", "Event Title")
        event.put("description", "Event Desc")
        event.put("eventLocation", "Event Location")

        Toast.makeText(this, "This Event has been added to your calendar", Toast.LENGTH_LONG)
            .show()

    }

    override fun onShareButtonClick(eventTitle: String, eventId: String, eventLocation: String, eventDate: String) {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        eventTitleToBeShared = eventTitle
        eventDateToBeShared = eventDate
        eventLocationToBeShared = eventLocation
        eventIDToBeShared = eventId
    }

    override fun onHomeFragmentAttached() {

        homeFab.visibility = VISIBLE
        homeFab.apply {
            setOnClickListener {

                startActivity(Intent(this@HomeActivity, AddEventPremActivity::class.java))
            }
            setImageDrawable(resources.getDrawable(io.neolution.eventify.R.drawable.ic_add_black_24dp))
        }
    }

    override fun onExploreFragmentAttached() {

        homeFab.visibility = GONE
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
    private lateinit var profileOptionsBottomSheet: LinearLayout
    private lateinit var profileBottomSheetBehaviour: BottomSheetBehavior<LinearLayout>

    private lateinit var eventTitleToBeShared: String
    private lateinit var eventIDToBeShared: String
    private lateinit var eventDateToBeShared: String
    private lateinit var eventLocationToBeShared: String

    private lateinit var closeShareEventBottomSheetBtn: ImageButton
    private lateinit var shareBsheetTukPicBtn: LinearLayout
    private lateinit var shareBsheetSocialMedia: LinearLayout
    private lateinit var shareBsheetCopyLink: LinearLayout

    private lateinit var closeProfileBottomSheetBtn: ImageButton
    private lateinit var editProfile: LinearLayout
    private lateinit var changeInterests: LinearLayout
    private lateinit var signOut: LinearLayout

    var registered = false

    private lateinit var homeFrameLayout: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        checkIfUserExits()

        homeFab = findViewById(R.id.home_fab)

        shareEventBottomSheet = findViewById(R.id.profile_options_bottomsheet)
        bottomSheetBehavior = BottomSheetBehavior.from(shareEventBottomSheet)
        profileOptionsBottomSheet = findViewById(R.id.share_event_bottomsheet)
        profileBottomSheetBehaviour = BottomSheetBehavior.from(profileOptionsBottomSheet)

        closeProfileBottomSheetBtn = findViewById(R.id.profile_opt_bsheet_close)
        editProfile = findViewById(R.id.profile_opt_bsheet_edit_profile)
        changeInterests = findViewById(R.id.profile_opt_bsheet_change_interests)
        signOut = findViewById(R.id.profile_opt_bsheet_signout)

        signOut.setOnClickListener {
            val dialog = android.support.v7.app.AlertDialog.Builder(this, R.style.MyTimePickerDialogTheme)
            dialog.setMessage("Are you sure you want to sign out??")
            dialog.setCancelable(false)
            dialog.setPositiveButton("Yes"){
                    _, _ ->
                AuthRepo.signOut()

                startActivity(Intent(this, SignInActivity::class.java))
                finish()

                indicate("Signed out!")

            }
            dialog.setNegativeButton("No"){
                    dialogInterface, _ ->

                dialogInterface.dismiss()
            }

            dialog.show()
        }

        editProfile.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
            profileBottomSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        changeInterests.setOnClickListener {
            val intent = Intent(this, TagsActivity::class.java)
            intent.putExtra("startedFrom", TagsActivity::class.java.simpleName)
            startActivity(intent)
            profileBottomSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        closeProfileBottomSheetBtn.setOnClickListener {
            profileBottomSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
        }

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

        FirebaseMessaging.getInstance().subscribeToTopic("Owner")
            .addOnCompleteListener { task ->

                if (!task.isSuccessful) {
                    Log.e(HomeActivity::class.java.simpleName, "Subscribed Unsuccessfully!")
                }else{
                    Log.e(HomeActivity::class.java.simpleName, "Subscribed Successfully!")
                }

            }

        homeFrameLayout = findViewById(R.id.home_frame_layout)
        supportFragmentManager.beginTransaction().
            replace(R.id.home_frame_layout, HomeFragment())
            .commitAllowingStateLoss()

        homeFab.setOnClickListener {
            startActivity(Intent(this@HomeActivity, AddEventPremActivity::class.java))
        }

        home_bottom_nav_bar.setOnNavigationItemSelectedListener {
            item ->
            when (item.itemId){
                R.id.main_menu_home -> {

                    supportFragmentManager.beginTransaction().
                        replace(R.id.home_frame_layout, HomeFragment())
                        .commitAllowingStateLoss()

                    return@setOnNavigationItemSelectedListener true
                }

                R.id.main_menu_explore -> {
                    supportFragmentManager.beginTransaction().
                        replace(R.id.home_frame_layout, ExploreFragment())
                        .commitAllowingStateLoss()
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.main_menu_update -> {
                    supportFragmentManager.beginTransaction().
                        replace(R.id.home_frame_layout, UpdatesFragment())
                        .commitAllowingStateLoss()
                    return@setOnNavigationItemSelectedListener true
                }

//                R.id.main_menu_trends -> {
//
//                }

                R.id.main_menu_profile -> {
                    supportFragmentManager.beginTransaction().
                        replace(R.id.home_frame_layout, Profile())
                        .commitAllowingStateLoss()

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
            startActivity(Intent(this, SignInActivity::class.java))
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


