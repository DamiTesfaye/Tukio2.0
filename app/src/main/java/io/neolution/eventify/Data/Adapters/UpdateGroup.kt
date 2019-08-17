package io.neolution.eventify.Data.Adapters

import android.view.View
import android.widget.TextView
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import io.neolution.eventify.R

class UpdateGroup(val updateText: String): Item<UpdateGroup.UpdateViewHolder>() {

    override fun bind(viewHolder: UpdateViewHolder, position: Int) {
        (viewHolder.view.findViewById<TextView>(R.id.update_viewholder_update_tv)).text = updateText
    }

    override fun getLayout() = R.layout.update_recycler_viewholder

    class UpdateViewHolder(val view: View): ViewHolder(view)
}