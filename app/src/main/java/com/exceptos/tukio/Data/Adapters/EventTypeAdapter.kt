package com.exceptos.tukio.Data.Adapters

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.exceptos.tukio.Data.ModelClasses.ChipModel
import com.exceptos.tukio.Listeners.OnEventTypeSelected
import com.exceptos.tukio.R

class EventTypeAdapter(var chipList: MutableList<ChipModel>, val context: Context, val onEventTypeSelected: OnEventTypeSelected): RecyclerView.Adapter<com.exceptos.tukio.Data.Adapters.EventTypeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): com.exceptos.tukio.Data.Adapters.EventTypeAdapter.ViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.chip_layout, parent, false)
        return com.exceptos.tukio.Data.Adapters.EventTypeAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: com.exceptos.tukio.Data.Adapters.EventTypeAdapter.ViewHolder, position: Int) {
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