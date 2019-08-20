package io.neolution.eventify.View.Fragments.HomeFragment

import android.content.Context
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import com.xwray.groupie.ViewHolder
import io.neolution.eventify.Data.Adapters.FullUpdateAdapter
import io.neolution.eventify.Data.ModelClasses.FullUpdateModel
import io.neolution.eventify.Data.ModelClasses.breakDocumentIntoEvntsModel
import io.neolution.eventify.Data.ModelClasses.breakDownToUpdatesModel
import io.neolution.eventify.Listeners.OnHomeFragmentsAttached
import io.neolution.eventify.R
import io.neolution.eventify.Repos.AuthRepo
import io.neolution.eventify.Repos.FireStoreRepo
import io.neolution.eventify.Utils.AppUtils
import io.neolution.eventify.databinding.FragmentUpdateBinding

/**
 * Created by Big-Nosed Developer on the Edge of Infinity.
 */
class UpdatesFragment: Fragment() {

    private lateinit var onHomeFragmentsAttached: OnHomeFragmentsAttached

    lateinit var binding: FragmentUpdateBinding
    lateinit var progressBar: ProgressBar
    lateinit var swipeLayout: SwipeRefreshLayout

    lateinit var firestoreRepo: FireStoreRepo
    lateinit var list: MutableList<FullUpdateModel>
    lateinit var adapter: FullUpdateAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_update, container!!, false)

        val manager = AppUtils.getRecycleLayout(AppUtils.LINEAR_LAYOUT_MANAGER, context!!)
        progressBar = binding.root.findViewById(R.id.frag_update_progress_bar)
        swipeLayout = binding.root.findViewById(R.id.frag_update_swipe_layout)

        swipeLayout.setOnRefreshListener {

            startLoadUpdates()

        }

        binding.fragUpdateRecycler.run {
            layoutManager = manager
        }

        firestoreRepo = FireStoreRepo()
        list = mutableListOf()

        adapter = FullUpdateAdapter(context!!, list )

        binding.fragUpdateRecycler.adapter = adapter

        startLoadUpdates()

        return binding.root
    }

    private fun startLoadUpdates() {
        progressBar.visibility = VISIBLE

        firestoreRepo.getEventPosts().addSnapshotListener(activity!!) { eventPostsSnapshot, _ ->
            if (eventPostsSnapshot != null && !eventPostsSnapshot.isEmpty){
                for (eventDoc in eventPostsSnapshot.documents){
                    val eventModel = eventDoc.breakDocumentIntoEvntsModel()

                    firestoreRepo.getDocumentLikesCollection(eventDoc.id).document(AuthRepo.getUserUid()).get().addOnSuccessListener {documentTask ->
                        if (documentTask.exists()){

                            //Only give updates for events that user is going for
                            firestoreRepo.getDocumentUpdatesPath(eventDoc.id).addSnapshotListener { updateSnapshot, _ ->
                                if (updateSnapshot != null && !updateSnapshot.isEmpty){
                                    for (updateDoc in updateSnapshot.documents){
                                        val model = updateDoc.breakDownToUpdatesModel()
                                        val eventName = eventModel.eventTitle

                                        val finalModel = FullUpdateModel(model, eventName)
                                        if(!list.contains(finalModel)){
                                            list.add(finalModel)
                                            adapter.notifyDataSetChanged()

                                            if (swipeLayout.isRefreshing){
                                                swipeLayout.isRefreshing = false
                                            }

                                        }

                                    }
                                }
                            }
                        }else{

                        }
                    }
                }

                if (list.isEmpty()){
                    progressBar.visibility = GONE
                    binding.fragUpdateRecycler.visibility = GONE
                    binding.fragUpdateEmptyFeedLayout.visibility = VISIBLE

                    if (swipeLayout.isRefreshing){
                        swipeLayout.isRefreshing = false
                    }
                }else{

                    binding.fragUpdateRecycler.visibility = VISIBLE
                    progressBar.visibility = GONE
                    binding.fragUpdateEmptyFeedLayout.visibility = GONE

                    if (swipeLayout.isRefreshing){
                        swipeLayout.isRefreshing = false
                    }

                }

            }
        }
    }

    override fun onStart() {
        super.onStart()

        startLoadUpdates()
    }

    override fun onResume() {
        super.onResume()

        startLoadUpdates()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        onHomeFragmentsAttached = context as OnHomeFragmentsAttached
        onHomeFragmentsAttached.onUpdateFragmentAttached()
    }
}
