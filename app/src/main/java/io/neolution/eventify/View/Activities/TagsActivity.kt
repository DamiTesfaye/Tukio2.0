package io.neolution.eventify.View.Activities

import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import android.view.View
import android.view.View.*
import io.neolution.eventify.Data.Adapters.TagsAdapter
import io.neolution.eventify.Data.ModelClasses.breakDownToUserModel
import io.neolution.eventify.Data.ViewModels.ProfileViewModel
import io.neolution.eventify.Listeners.OnChipSelected
import io.neolution.eventify.R
import io.neolution.eventify.Repos.AuthRepo
import io.neolution.eventify.Repos.FireStoreRepo
import io.neolution.eventify.Utils.AppUtils
import io.neolution.eventify.databinding.ActivityTagsBinding
import kotlinx.android.synthetic.main.activity_tags.*

class TagsActivity : AppCompatActivity(), OnChipSelected, View.OnClickListener {
    override fun onClick(view: View?) {
        val id = view!!.id

        when(id){
            R.id.tags_save_interests_text -> {
                if(tagsArray.isNotEmpty()){
                    startUpdatingTags(tagsArray)
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
    }

    lateinit var binding: ActivityTagsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tags)
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)

        binding.tagsCloseActivity.setOnClickListener(this)
        binding.tagsSaveInterestsText.setOnClickListener(this)

        val startedFrom = intent.getStringExtra("startedFrom")
        if (startedFrom == AuthActivity::class.java.simpleName){
            binding.tagsCloseActivity.visibility = INVISIBLE
            binding.tagsCloseActivity.isEnabled = false
        }

        val chipList= AppUtils.createChipList()

        if (AuthRepo.getCurrentUser() != null ){
            FireStoreRepo().getCurrentUserDocumentPath().get().addOnSuccessListener { snapshot ->
                if (snapshot != null && snapshot.exists()){
                    val userModel = snapshot.breakDownToUserModel()
                    val userTags = userModel.tags

                    if (userTags != null) {

                        adapter = TagsAdapter(chipList, this, userTags, this)

                        val recyclerView = binding.tagsContainer
                        val manager = StaggeredGridLayoutManager(
                            8,
                            StaggeredGridLayoutManager.HORIZONTAL
                        )
                        manager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE

                        recyclerView.apply {
                            layoutManager = manager
                            setHasFixedSize(true)
                        }

                        recyclerView.adapter = adapter
                        tags_circular_progress_bar.visibility = GONE

                    } else{
                        adapter = TagsAdapter(chipList, this, null, this)

                        val recyclerView = binding.tagsContainer
                        val manager = StaggeredGridLayoutManager(
                            8,
                            StaggeredGridLayoutManager.HORIZONTAL
                        )
                        manager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE

                        recyclerView.apply{
                            layoutManager = manager
                            setHasFixedSize(true)
                        }

                        recyclerView.adapter = adapter
                        tags_circular_progress_bar.visibility = GONE
                    }
                }

            }.addOnFailureListener {

                AppUtils.getCustomSnackBar(findViewById<View>(android.R.id.content), "Failed to load already selected tags. Check your internet connection and try again..", this)
                    .show()

                adapter = TagsAdapter(chipList, this, null, this)

                val recyclerView = binding.tagsContainer
                val manager = StaggeredGridLayoutManager(
                    8,
                    StaggeredGridLayoutManager.HORIZONTAL
                )
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

    private fun startUpdatingTags(tagList: MutableList<String>){

        if (tagList.size <= 5){

            binding.tagsSaveInterestsContainer.background = ContextCompat.getDrawable(this, R.drawable.buttonbg_outline)
            binding.tagsSaveInterestsProgress.visibility = VISIBLE
            binding.tagsSaveInterestsText.visibility = GONE

            profileViewModel.updateInterestedTags(tagList,{

                binding.tagsSaveInterestsContainer.background = ContextCompat.getDrawable(this, R.drawable.buttonbg)
                binding.tagsSaveInterestsProgress.visibility = GONE
                binding.tagsSaveInterestsText.visibility = VISIBLE

                AppUtils.getCustomSnackBar(findViewById<View>(android.R.id.content), it, this)
                    .show()
            }, {

                startActivity(Intent(this, HomeActivity::class.java))
                finish()

            })
        }else{
            AppUtils.getCustomSnackBar(findViewById<View>(android.R.id.content), "Only a maximum of 5 tags are allowed. Please reselect", this)
                .show()
        }
    }
}
