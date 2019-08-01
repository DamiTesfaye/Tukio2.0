package io.neolution.eventify.Data.Adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import de.hdodenhof.circleimageview.CircleImageView
import io.neolution.eventify.Data.ModelClasses.ChipModel
import io.neolution.eventify.Listeners.OnEventTypeSelected
import io.neolution.eventify.R

class EventTypeAdapter(var chipList: MutableList<ChipModel>, val context: Context, val onEventTypeSelected: OnEventTypeSelected): RecyclerView.Adapter<EventTypeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.chip_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chipTextView = holder.itemView.findViewById<TextView>(R.id.chip_layout_text)
        val chipImageView = holder.itemView.findViewById<ImageView>(R.id.chip_layout_image)

        val chipText = chipList[position].chipText
        val chipImage = chipList[position].imageResource

        chipTextView.text = chipText
        chipImageView.setImageDrawable(ContextCompat.getDrawable(context, chipImage))

        holder.itemView.setOnClickListener {
            onEventTypeSelected.onEventTypeClicked(chipText)
        }
    }

    override fun getItemCount() = chipList.size

    class ViewHolder(val view: View): RecyclerView.ViewHolder(view)

}