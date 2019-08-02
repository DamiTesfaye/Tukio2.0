package io.neolution.eventify.View.Fragments.HomeFragment


import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import de.hdodenhof.circleimageview.CircleImageView
import io.neolution.eventify.Data.Adapters.OnBoardingAdapter
import io.neolution.eventify.Data.ModelClasses.breakDownToUserModel
import io.neolution.eventify.Listeners.OnEditProfileClicked
import io.neolution.eventify.R
import io.neolution.eventify.Repos.AuthRepo
import io.neolution.eventify.Repos.FireStoreRepo

class Profile : Fragment() {

    private lateinit var fireStoreRepo: FireStoreRepo
    private lateinit var onEditProfileClicked: OnEditProfileClicked
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_profile, container, false)

        val userImage = view.findViewById<CircleImageView>(R.id.profile_frag_userimage)
        val userNameTextView = view.findViewById<TextView>(R.id.profile_frag_username)
        val userBioTextView = view.findViewById<TextView>(R.id.profile_frag_user_bio)

        val list = mutableListOf<Fragment>()
        list.add(PostedEventsFragment())
        list.add(PromotedEventsFragment())

        val profileTabLayout = view.findViewById<TabLayout>(R.id.profile_frag_tab_layout)
        val moreImageButton = view.findViewById<ImageButton>(R.id.profile_frag_menu_btn)
        val profileViewPager = view.findViewById<ViewPager>(R.id.profile_frag_viewpager)
        onEditProfileClicked = activity!! as OnEditProfileClicked

        moreImageButton.setOnClickListener {
            onEditProfileClicked.onEditButtonClicked()
        }

        val adapter = OnBoardingAdapter(list, activity!!.supportFragmentManager)
        profileViewPager.adapter = adapter
        profileTabLayout.setupWithViewPager(profileViewPager)

        fireStoreRepo = FireStoreRepo()
        val userDetails = fireStoreRepo.getUserAccountDetailsFromUid(AuthRepo.getUserUid())
        userDetails.addSnapshotListener { snapshot, _ ->
            if (snapshot != null && snapshot.exists()){
                val userModel = snapshot.breakDownToUserModel()
                userNameTextView.text= userModel.userName
                userBioTextView.text = userModel.userBio

                val requestOptions = RequestOptions()
                requestOptions.placeholder(ContextCompat.getDrawable(context!!, R.drawable.ic_default_user))
                val thumbNailRequest = Glide.with(context!!.applicationContext).load(userModel.userThumbLink)

                Glide.with(context!!.applicationContext)
                    .setDefaultRequestOptions(requestOptions)
                    .asDrawable()
                    .load(userModel.userImage)
                    .thumbnail(thumbNailRequest)
                    .into(userImage)
            }
        }

        return view
    }


}
