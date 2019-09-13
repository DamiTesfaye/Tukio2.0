package com.exceptos.tukio.Data.Adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.exceptos.tukio.Data.ModelClasses.GuestModel
import com.exceptos.tukio.R

/**
 * Created by Big-Nosed Developer on the Edge of Infinity.
 */
class GuestsAdapter(val context: Context, val listOfGuests: List<GuestModel>): RecyclerView.Adapter<com.exceptos.tukio.Data.Adapters.GuestsAdapter.GuestViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): com.exceptos.tukio.Data.Adapters.GuestsAdapter.GuestViewHolder {
        val view  = LayoutInflater.from(context).inflate(R.layout.speaker_layout, parent, false)
        return com.exceptos.tukio.Data.Adapters.GuestsAdapter.GuestViewHolder(view)
    }

    override fun getItemCount() =  listOfGuests.size


    override fun onBindViewHolder(holder: com.exceptos.tukio.Data.Adapters.GuestsAdapter.GuestViewHolder, position: Int) {
        val currentGuest = listOfGuests[position]
        holder.itemView.findViewById<TextView>(R.id.speaker_layout_name_tv).text = currentGuest.guestName
        holder.itemView.findViewById<TextView>(R.id.speaker_layout_bio_tv).text = currentGuest.guestBio

    }

    class GuestViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)


}