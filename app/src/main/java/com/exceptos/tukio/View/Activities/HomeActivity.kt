package com.exceptos.tukio.View.Activities

import android.content.*
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import com.exceptos.tukio.Data.ModelClasses.breakDownToUserModel
import com.exceptos.tukio.Data.ModelClasses.indicate
import com.exceptos.tukio.Listeners.OnAddReminderClicked
import com.exceptos.tukio.Listeners.OnEditProfileClicked
import com.exceptos.tukio.Listeners.OnHomeFragmentsAttached
import com.exceptos.tukio.Listeners.OnShareEventClicked
import com.exceptos.tukio.R
import com.exceptos.tukio.Repos.AuthRepo
import com.exceptos.tukio.Repos.FireStoreRepo
import com.exceptos.tukio.Utils.AppUtils
import com.exceptos.tukio.Utils.FirebaseUtils
import com.exceptos.tukio.Utils.IntentUtils
import com.exceptos.tukio.View.Fragments.HomeFragment.ExploreFragment
import com.exceptos.tukio.View.Fragments.HomeFragment.HomeFragment
import com.exceptos.tukio.View.Fragments.HomeFragment.ProfileFragment
import com.exceptos.tukio.View.Fragments.HomeFragment.UpdatesFragment
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : AppCompatActivity(),  OnHomeFragmentsAttached, OnShareEventClicked, OnAddReminderClicked, OnEditProfileClicked {

    override fun onEditButtonClicked() {
        profileBottomSheetBehaviour.state = BottomSheetBehavior.STATE_EXPANDED
        home_bottom_sheet_bg.visibility = VISIBLE
    }

    override fun onTrendsFragmentAttached() {

    }

    override fun onProfileFragmentAttached() {
        findViewById<FloatingActionButton>(R.id.home_fab)?.apply {
            hide()
        }
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
        home_bottom_sheet_bg.visibility = VISIBLE

        eventTitleToBeShared = eventTitle
        eventDateToBeShared = eventDate
        eventLocationToBeShared = eventLocation
        eventIDToBeShared = eventId
    }

    override fun onHomeFragmentAttached() {

        findViewById<FloatingActionButton>(R.id.home_fab)?.apply {
            show()
            setOnClickListener {

                startActivity(Intent(this@HomeActivity, AddEventPremActivity::class.java))
            }
            setImageDrawable(ContextCompat.getDrawable(this@HomeActivity, R.drawable.ic_add_black_24dp))
        }
    }

    override fun onExploreFragmentAttached() {

        findViewById<FloatingActionButton>(R.id.home_fab)?.apply {
            show()
            setOnClickListener {

                startActivity(Intent(this@HomeActivity, AddEventPremActivity::class.java))
            }
            setImageDrawable(ContextCompat.getDrawable(this@HomeActivity, R.drawable.ic_add_black_24dp))
        }

    }

    override fun onUpdateFragmentAttached() {

        findViewById<FloatingActionButton>(R.id.home_fab)?.apply {
            show()
            setOnClickListener {

                startActivity(Intent(this@HomeActivity, AddEventPremActivity::class.java))
            }
            setImageDrawable(ContextCompat.getDrawable(this@HomeActivity, R.drawable.ic_add_black_24dp))
        }

    }

    override fun onEventTypeSelected(eventType: String) {
        val contentView = findViewById<View>(R.id.home_container)
        val snackBar = Snackbar.make(contentView, "Loading $eventType events..", Snackbar.LENGTH_SHORT)

        val view = snackBar.view
        view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
        val textView = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))

        val layoutParams = view.layoutParams as CoordinatorLayout.LayoutParams

        layoutParams.anchorId = R.id.home_bottom_nav_bar //Id for your bottomNavBar or TabLayout
        layoutParams.anchorGravity = Gravity.TOP
        layoutParams.gravity = Gravity.TOP
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            snackBar.view.elevation = 0F
        }
        snackBar.view.layoutParams = layoutParams

        snackBar.show()

    }

    private lateinit var bottomNavBar: BottomNavigationView
    lateinit var adapter: com.exceptos.tukio.Data.Adapters.HomePagerAdapter
    lateinit var broadcast: InternetBroadcast
    lateinit var filter: IntentFilter
    lateinit var homeFab: FloatingActionButton

    private lateinit var shareEventBottomSheet: LinearLayout
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var profileOptionsBottomSheet: LinearLayout
    private lateinit var profileBottomSheetBehaviour: BottomSheetBehavior<LinearLayout>
    private lateinit var homeViewPager: ViewPager

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
    private lateinit var aboutUs: LinearLayout

    var registered = false
    private var currentFragNo = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        checkIfUserExits()

        homeFab = findViewById(R.id.home_fab)

        shareEventBottomSheet = findViewById(R.id.profile_options_bottomsheet)
        homeViewPager = findViewById(R.id.home_viewpager)
        bottomSheetBehavior = BottomSheetBehavior.from(shareEventBottomSheet)
        profileOptionsBottomSheet = findViewById(R.id.share_event_bottomsheet)
        profileBottomSheetBehaviour = BottomSheetBehavior.from(profileOptionsBottomSheet)

        closeProfileBottomSheetBtn = findViewById(R.id.profile_opt_bsheet_close)
        editProfile = findViewById(R.id.profile_opt_bsheet_edit_profile)
        changeInterests = findViewById(R.id.profile_opt_bsheet_change_interests)
        signOut = findViewById(R.id.profile_opt_bsheet_signout)
        aboutUs = findViewById(R.id.profile_opt_bsheet_about_us)

        aboutUs.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
            profileBottomSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        val fragList = listOf(HomeFragment(), ExploreFragment(), UpdatesFragment(), ProfileFragment())
        val adapter = com.exceptos.tukio.Data.Adapters.OnBoardingAdapter(fragList, supportFragmentManager)

        homeViewPager.adapter = adapter
        homeViewPager.setCurrentItem(0, false)

        signOut.setOnClickListener {
            val dialog = androidx.appcompat.app.AlertDialog.Builder(this, R.style.MyTimePickerDialogTheme)
            dialog.setMessage("Are you sure you want to sign out??")
            dialog.setCancelable(false)
            dialog.setPositiveButton("Yes"){
                    _, _ ->
                AuthRepo.signOut()

                startActivity(Intent(this, AuthActivity::class.java))
                finish()

                indicate("Signed out!")

            }
            dialog.setNegativeButton("No"){
                    dialogInterface, _ ->

                dialogInterface.dismiss()
            }

            dialog.show()
        }

        profileBottomSheetBehaviour.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                home_bottom_sheet_bg.visibility = VISIBLE
                home_bottom_sheet_bg.alpha = slideOffset
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState== BottomSheetBehavior.STATE_COLLAPSED){
                    home_bottom_sheet_bg.visibility = GONE
                }
            }
        })

        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                home_bottom_sheet_bg.visibility = VISIBLE
                home_bottom_sheet_bg.alpha = slideOffset
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState== BottomSheetBehavior.STATE_COLLAPSED){
                    home_bottom_sheet_bg.visibility = GONE
                }
            }
        })

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

        home_bottom_sheet_bg.setOnClickListener {
            profileBottomSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
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
        shareBsheetCopyLink.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            AppUtils.copyTextToClipBoard(AppUtils.createEventLink(eventIDToBeShared), this)

        }

        val currentUserId = AuthRepo.getUserUid()
        updateUid(currentUserId)

        broadcast = InternetBroadcast()

        filter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(broadcast, filter)

        if (AuthRepo.getCurrentUser() != null){
            FireStoreRepo().getCurrentUserDocumentPath().addSnapshotListener(this) {
                    snapshot, _ ->
                if (snapshot != null && snapshot.exists()){
                    val userModel = snapshot.breakDownToUserModel()
                    FirebaseUtils().saveUserDetails(userModel, this@HomeActivity)

                }
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



        homeFab.setOnClickListener {
            startActivity(Intent(this@HomeActivity, AddEventPremActivity::class.java))
        }

        bottomNavBar = findViewById<BottomNavigationView>(R.id.home_bottom_nav_bar)

//        supportFragmentManager.beginTransaction().
//            replace(R.id.home_frame_layout, HomeFragment(), "HOME_FRAGMENT")
//            .commit()

        bottomNavBar.setOnNavigationItemSelectedListener {
                item ->
            when (item.itemId){
                R.id.main_menu_home -> {

//                    supportFragmentManager.beginTransaction().
//                        replace(R.id.home_frame_layout, HomeFragment(), "HOME_FRAGMENT")
//                        .commit()

                    homeViewPager.setCurrentItem(0, false)
                    currentFragNo = 0

                    return@setOnNavigationItemSelectedListener true
                }

                R.id.main_menu_explore -> {
//                    supportFragmentManager.beginTransaction().
//                        replace(R.id.home_frame_layout, ExploreFragment(), "EXPLORE_FRAGMENT")
//                        .commit()

                    homeViewPager.setCurrentItem(1, false)
                    currentFragNo = 1
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.main_menu_update -> {
//                    supportFragmentManager.beginTransaction().
//                        replace(R.id.home_frame_layout, UpdatesFragment(), "UPDATE_FRAGMENT")
//                        .commit()

                    homeViewPager.setCurrentItem(2, false)
                    currentFragNo = 2
                    return@setOnNavigationItemSelectedListener true
                }

//                R.id.main_menu_trends -> {
//
//                }

                R.id.main_menu_profile -> {
//                    supportFragmentManager.beginTransaction().
//                        replace(R.id.home_frame_layout, ProfileFragment(), "PROFILE_FRAGMENT")
//                        .commit()

                    homeViewPager.setCurrentItem(3, false)
                    currentFragNo = 3

                    return@setOnNavigationItemSelectedListener true
                }

                else -> {
                    return@setOnNavigationItemSelectedListener true
                }

            }
        }

//        if (savedInstanceState != null){
//            val currentFragment = savedInstanceState.getInt("currentFragment", 0)
//            if (currentFragment != 0){
//                Toast.makeText(this, "$currentFragment", Toast.LENGTH_LONG)
//                    .show()
//                when(currentFragment){
//                    1 -> {
//                        supportFragmentManager.beginTransaction().
//                            replace(R.id.home_frame_layout, ExploreFragment(), "EXPLORE_FRAGMENT")
//                            .commit()
//                    }
//
//                    2 -> {
//                        supportFragmentManager.beginTransaction().
//                            replace(R.id.home_frame_layout, UpdatesFragment(), "UPDATE_FRAGMENT")
//                            .commit()
//                    }
//
//                    3 -> {
//                        supportFragmentManager.beginTransaction().
//                            replace(R.id.home_frame_layout, ProfileFragment(), "PROFILE_FRAGMENT")
//                            .commit()
//                    }
//                }
//            }
//        }

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
            finish()
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt("currentFragment", currentFragNo)
    }

}


