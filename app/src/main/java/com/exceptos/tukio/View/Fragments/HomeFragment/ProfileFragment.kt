package com.exceptos.tukio.View.Fragments.HomeFragment


import android.content.Context
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.exceptos.tukio.Data.ModelClasses.breakDownToUserModel
import com.exceptos.tukio.Data.ViewModels.EventsViewModel
import com.exceptos.tukio.Listeners.OnEditProfileClicked
import com.exceptos.tukio.Listeners.OnHomeFragmentsAttached
import com.exceptos.tukio.R
import com.exceptos.tukio.Repos.AuthRepo
import com.exceptos.tukio.Repos.FireStoreRepo

class ProfileFragment : Fragment() {

    private lateinit var fireStoreRepo: FireStoreRepo
    private lateinit var onEditProfileClicked: OnEditProfileClicked
    private lateinit var onHomeFragmentsAttached: OnHomeFragmentsAttached

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_profile, container, false)

        val userImage = view.findViewById<ImageView>(R.id.profile_frag_userimage)
        val userNameTextView = view.findViewById<TextView>(R.id.profile_frag_username)
        val userBioTextView = view.findViewById<TextView>(R.id.profile_frag_user_bio)
        val postedEventsNum = view.findViewById<TextView>(R.id.profile_frag_posted_events_num)
        val promotedEventsNum = view.findViewById<TextView>(R.id.profile_frag_promoted_events_num)

        val list = mutableListOf<Fragment>()
        list.add(PostedEventsFragment())
        list.add(PromotedEventsFragment())
        list.add(PinnedEventsFragment())

        val profileTabLayout = view.findViewById<TabLayout>(R.id.profile_frag_tab_layout)
        val moreImageButton = view.findViewById<ImageButton>(R.id.profile_frag_menu_btn)
        val profileViewPager = view.findViewById<ViewPager>(R.id.profile_frag_viewpager)
        onEditProfileClicked = activity!! as OnEditProfileClicked

        moreImageButton.setOnClickListener {
            onEditProfileClicked.onEditButtonClicked()
        }

        val adapter = com.exceptos.tukio.Data.Adapters.OnBoardingAdapter(list, activity!!.supportFragmentManager)
        profileViewPager.adapter = adapter
        profileTabLayout.setupWithViewPager(profileViewPager)

        userBioTextView.setTextColor(ContextCompat.getColor(context!!, android.R.color.darker_gray))

        fireStoreRepo = FireStoreRepo()

        if (AuthRepo.getCurrentUser() != null ){
            val userDetails = fireStoreRepo.getUserAccountDetailsFromUid(AuthRepo.getUserUid())
            userDetails.addSnapshotListener(activity!!) { snapshot, _ ->
                if (snapshot != null && snapshot.exists()){
                    val userModel = snapshot.breakDownToUserModel()
                    userNameTextView.text= userModel.userName
                    if (userModel.userBio.isEmpty()){
                        userBioTextView.text = "No Bio Set."
                    }else{
                        userBioTextView.text = userModel.userBio
                    }

                    val requestOptions = RequestOptions()
                    if (context != null){
                        requestOptions.placeholder(ContextCompat.getDrawable(context!!.applicationContext, R.drawable.ic_male_placeholder))
                        val thumbNailRequest = Glide.with(context!!.applicationContext).load(userModel.userThumbLink)

                        Glide.with(context!!.applicationContext)
                            .setDefaultRequestOptions(requestOptions)
                            .asDrawable()
                            .load(userModel.userImage)
                            .thumbnail(thumbNailRequest)
                            .into(userImage)
                    }
                }
            }
        }



        val eventViewModel = ViewModelProviders.of(this).get(EventsViewModel::class.java)
        eventViewModel.getEventDocuments().whereEqualTo("userUID", AuthRepo.getUserUid())
            .addSnapshotListener { snapshot, _ ->

                if (snapshot != null && !snapshot.isEmpty) {
                    postedEventsNum.text = (snapshot.documents.size).toString()
                }else{
                    postedEventsNum.text = ("0")
                }
            }

        eventViewModel.getPromotedEvents().whereEqualTo("userUID", AuthRepo.getUserUid())
            .addSnapshotListener { snapshot, _ ->

                if (snapshot != null && !snapshot.isEmpty) {
                    promotedEventsNum.text = (snapshot.documents.size).toString()
                }else{
                    promotedEventsNum.text = ("0")
                }
            }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        onHomeFragmentsAttached = context as OnHomeFragmentsAttached
        onHomeFragmentsAttached.onProfileFragmentAttached()
    }

}
