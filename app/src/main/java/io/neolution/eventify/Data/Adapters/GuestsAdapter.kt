package io.neolution.eventify.Data.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.neolution.eventify.Data.ModelClasses.GuestModel
import io.neolution.eventify.R

/**
 * Created by Big-Nosed Developer on the Edge of Infinity.
 */
class GuestsAdapter(val context: Context, val listOfGuests: List<GuestModel>): RecyclerView.Adapter<GuestsAdapter.GuestViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuestViewHolder {
        val view  = LayoutInflater.from(context).inflate(R.layout.speaker_layout, parent, false)
        return GuestViewHolder(view)
    }

    override fun getItemCount() =  listOfGuests.size


    override fun onBindViewHolder(holder: GuestViewHolder, position: Int) {
        val currentGuest = listOfGuests[position]
        holder.itemView.findViewById<TextView>(R.id.speaker_layout_name_tv).text = currentGuest.guestName
        holder.itemView.findViewById<TextView>(R.id.speaker_layout_bio_tv).text = currentGuest.guestBio

    }

    class GuestViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)


}