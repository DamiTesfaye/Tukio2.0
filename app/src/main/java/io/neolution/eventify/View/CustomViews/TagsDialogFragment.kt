package io.neolution.eventify.View.CustomViews

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import io.neolution.eventify.Data.Adapters.TagsDialogAdapter
import io.neolution.eventify.Data.ModelClasses.indicate
import io.neolution.eventify.Listeners.OnChipSelected
import io.neolution.eventify.Listeners.OnEventTagsSelected
import io.neolution.eventify.R

/**
 * Created by Big-Nosed Developer on the Edge of Infinity.
 */
class TagsDialogFragment: DialogFragment(), OnChipSelected {

    private val tagsArray = mutableListOf<String>()
    lateinit var onTagsSelected: OnEventTagsSelected

    override fun onChipSelected(chipText: String) {
        if (tagsArray.size <= 5){
            tagsArray.add(chipText)
        }else{
            context?.indicate("Only the first five tags")
        }
    }

    override fun onChipDeselected(chipText: String) {
        if (tagsArray.contains(chipText)) tagsArray.remove(chipText)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.tags_dialog_fragment, container, false)

        val manager = StaggeredGridLayoutManager(8, StaggeredGridLayoutManager.HORIZONTAL)
        manager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE

        val tagsRecycler = view.findViewById<RecyclerView>(R.id.tags_dialog_fragment_recycler)
        tagsRecycler.run {
            setHasFixedSize(true)
            layoutManager = manager
        }

        val doneBtn = view.findViewById<Button>(R.id.tags_dialog_fragment_done_btn)

        val stringList = mutableListOf("Entertainment", "Sports",
            "Tech", "Movements", "MeetUps" , "Business",
            "Concerts", "Conventions/Conferences", "Parties", "Galas", "Motivation",
            "Skills and Acquisitions", "Workshops/Seminars",
            "Meetings", "Birthdays", "Sports",
            "Christian", "Muslim", "Football",
            "Basketball", "Athletics",
            "School", "Convocations", "Valedictory Service" , "Relationship",
            "Festivals", "Tests", "Examination", "Extra-Credits",
            "Send-Offs")

        val adapter = TagsDialogAdapter(stringList, context!!, this)
        tagsRecycler.adapter = adapter

        doneBtn.setOnClickListener {
            onTagsSelected.onTagsSelected(tagsArray)
        }

        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        onTagsSelected = context as OnEventTagsSelected

    }

    override fun onStart() {
        super.onStart()

        val dialog = dialog
        dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}