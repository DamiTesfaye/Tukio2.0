package io.neolution.eventify.View.Activities

import android.app.ActivityOptions
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.widget.LinearLayout
import com.github.clans.fab.FloatingActionButton
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.messaging.FirebaseMessaging
import io.neolution.eventify.Data.Adapters.HomePagerAdapter
import io.neolution.eventify.Data.ModelClasses.UserModel
import io.neolution.eventify.Data.ModelClasses.breakDownToUserModel
import io.neolution.eventify.Data.ModelClasses.indicate
import io.neolution.eventify.Data.ViewModels.ProfileViewModel
import io.neolution.eventify.Listeners.OnHomeFragmentAttached
import io.neolution.eventify.Listeners.OnShareEventClicked
import io.neolution.eventify.R
import io.neolution.eventify.Repos.AuthRepo
import io.neolution.eventify.Repos.FireStoreRepo
import io.neolution.eventify.Repos.FirebaseStorageRepo
import io.neolution.eventify.Services.GoogleServicesClass
import io.neolution.eventify.Utils.FirebaseUtils
import io.neolution.eventify.View.CustomViews.CustomViewPager
import io.neolution.eventify.View.Fragments.HomeFragment.ExploreFragment
import io.neolution.eventify.View.Fragments.HomeFragment.HomeFragment
import io.neolution.eventify.View.Fragments.HomeFragment.UpdatesFragment
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(),  OnHomeFragmentAttached, OnShareEventClicked {

    override fun onShareButtonClick(eventTitle: String, eventId: String, eventLocation: String, eventDate: String) {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onHomeFragmentAttached() {

        home_fab.apply {
            setOnClickListener {

                startActivity(Intent(this@HomeActivity, AddEventActivity::class.java))
            }
            setImageDrawable(resources.getDrawable(R.drawable.ic_add_black_24dp))
        }
    }

    override fun onExploreFragmentAttached() {

        homeFab.visibility = GONE
        home_fab.visibility = GONE
        home_fab.apply {
            setOnClickListener {
                startActivity(Intent(this@HomeActivity, AddEventActivity::class.java))
            }
            setImageDrawable(resources.getDrawable(R.drawable.ic_add_black_24dp))
        }
    }

    override fun onUpdateFragmentAttached() {
        supportActionBar?.show()
    }

    lateinit var adapter: HomePagerAdapter
    lateinit var broadcast: InternetBroadcast
    lateinit var filter: IntentFilter
    lateinit var homeFab: FloatingActionButton
    private lateinit var shareEventBottomSheet: LinearLayout
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    var isConnected = false
    var registered = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        checkIfUserExits()

        homeFab = findViewById(R.id.home_fab)
        shareEventBottomSheet = findViewById(R.id.share_event_bottomsheet)
        bottomSheetBehavior = BottomSheetBehavior.from(shareEventBottomSheet)

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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(Intent(this@HomeActivity, AddEventActivity::class.java), ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            }else{
                startActivity(Intent(this@HomeActivity, AddEventActivity::class.java))
            }
        }

        promoted_fab.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(Intent(this@HomeActivity, AddPromotedEventActivity::class.java), ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            }else{
                startActivity(Intent(this@HomeActivity, AddPromotedEventActivity::class.java))
            }
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


