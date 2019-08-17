package io.neolution.eventify.Data.Adapters

import android.view.View
import android.widget.TextView
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import io.neolution.eventify.R

class SectionGroup(val sectionText: String): Item<SectionGroup.UpdateViewHolder>() {

    override fun bind(viewHolder: UpdateViewHolder, position: Int) {
        (viewHolder.view as TextView).text = sectionText
    }

    override fun getLayout() = R.layout.section_header

    class UpdateViewHolder(val view: View): ViewHolder(view)
}