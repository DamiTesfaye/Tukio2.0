package io.neolution.eventify.View.Activities

import android.app.ProgressDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import android.view.View.*
import io.neolution.eventify.Data.Adapters.TagsAdapter
import io.neolution.eventify.Data.ModelClasses.ChipModel
import io.neolution.eventify.Data.ModelClasses.UserModel
import io.neolution.eventify.Data.ModelClasses.breakDownToUserModel
import io.neolution.eventify.Data.ModelClasses.indicate
import io.neolution.eventify.Data.ViewModels.ProfileViewModel
import io.neolution.eventify.Listeners.OnChipSelected
import io.neolution.eventify.R
import io.neolution.eventify.Repos.FireStoreRepo
import io.neolution.eventify.Utils.AppUtils
import io.neolution.eventify.View.Fragments.AuthFragment.SignupFragments
import io.neolution.eventify.databinding.ActivityTagsBinding
import kotlinx.android.synthetic.main.activity_tags.*

class TagsActivity : AppCompatActivity(), OnChipSelected, View.OnClickListener {
    override fun onClick(view: View?) {
        val id = view!!.id

        when(id){
            R.id.tags_save_interests_text -> {
                if(tagsArray.isNotEmpty()){

                    binding.tagsSaveInterestsContainer.background = ContextCompat.getDrawable(this, R.drawable.buttonbg_outline)
                    binding.tagsSaveInterestsProgress.visibility = VISIBLE
                    binding.tagsSaveInterestsText.visibility = GONE

                    profileViewModel.updateInterestedTags(tagsArray,{

                        binding.tagsSaveInterestsContainer.background = ContextCompat.getDrawable(this, R.drawable.buttonbg)
                        binding.tagsSaveInterestsProgress.visibility = GONE
                        binding.tagsSaveInterestsText.visibility = VISIBLE

                        AppUtils.getCustomSnackBar(findViewById<View>(android.R.id.content), "Please select a tag", this)
                            .show()
                    }, {

                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()

                    })
                }else {

                    AppUtils.getCustomSnackBar(findViewById<View>(android.R.id.content), "Please select a tag", this)
                        .show()
                }
            }

            R.id.tags_close_activity -> {
               finish()
            }

        }
    }

    private val tagsArray = mutableListOf<String>()
    private lateinit var profileViewModel: ProfileViewModel
    lateinit var adapter: TagsAdapter

    override fun onChipSelected(chipText: String) {

        if (tagsArray.size <= 5){
            tagsArray.add(chipText)
        }
    }

    override fun onChipDeselected(chipText: String) {
        if(tagsArray.contains(chipText))tagsArray.remove(chipText)
        if (tagsArray.isEmpty()){

        }
    }

    lateinit var binding: ActivityTagsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tags)
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)


        binding.tagsCloseActivity.setOnClickListener(this)
        binding.tagsSaveInterestsText.setOnClickListener(this)

        val startedFrom = intent.getStringExtra("startedFrom")
        if (startedFrom == SignupFragments::class.java.simpleName){
            binding.tagsCloseActivity.visibility = INVISIBLE
            binding.tagsCloseActivity.isEnabled = false
        }

        // val chipList = mutableListOf("Entertainment", "Sports",
        ////            "Tech", "Movements", "MeetUps" , "Business",
        ////            "Concerts", "Conventions/Conferences", "Parties", "Galas", "Motivation",
        ////            "Skills and Acquisitions", "Workshops/Seminars",
        ////            "Meetings", "Birthdays", "Sports",
        ////             "Christian", "Muslim", "Football",
        ////            "Basketball", "Athletics",
        ////            "School", "Convocations", "Valedictory Service" , "Relationship",
        ////            "Festivals", "Tests", "Examination", "Extra-Credits",
        ////            "Send-Offs")

        val chipList = mutableListOf(
            ChipModel("Tech", R.drawable.ic_robot),
            ChipModel("Movement", R.drawable.ic_robot)
            ,ChipModel("MeetUp", R.drawable.ic_robot),
            ChipModel("Business", R.drawable.ic_robot),
            ChipModel("Concerts", R.drawable.ic_stage)
            ,ChipModel("Conventions/Conferences", R.drawable.ic_robot),
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

        FireStoreRepo().getCurrentUserDocumentPath().addSnapshotListener(this){
            snapshot, _ ->

            if (snapshot != null && snapshot.exists()){
                val userModel = snapshot.breakDownToUserModel()
                val userTags = userModel.tags

                if (userTags != null){

                    adapter = TagsAdapter(chipList, this, userTags, this)

                    val recyclerView = binding.tagsContainer
                    val manager = StaggeredGridLayoutManager(7, StaggeredGridLayoutManager.HORIZONTAL)
                    manager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE

                    recyclerView.apply{
                        layoutManager = manager
                        setHasFixedSize(true)
                    }

                    recyclerView.adapter = adapter
                    tags_circular_progress_bar.visibility = GONE

                }else{

                    adapter = TagsAdapter(chipList, this, null, this)

                    val recyclerView = binding.tagsContainer
                    val manager = StaggeredGridLayoutManager(7, StaggeredGridLayoutManager.HORIZONTAL)
                    manager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE

                    recyclerView.apply{
                        layoutManager = manager
                        setHasFixedSize(true)
                    }

                    recyclerView.adapter = adapter
                    tags_circular_progress_bar.visibility = GONE

                }
            }

        }
    }
}
