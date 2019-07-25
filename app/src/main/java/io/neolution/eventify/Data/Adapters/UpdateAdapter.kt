package io.neolution.eventify.Data.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import io.neolution.eventify.Data.ModelClasses.FullUpdateModel
import io.neolution.eventify.Data.ModelClasses.UpdatesModel
import io.neolution.eventify.R
import io.neolution.eventify.View.Activities.UpdatesActivity

/**
 * Created by Big-Nosed Developer on the Edge of Infinity.
 */
class UpdateAdapter(val context: Context, val listOfUpdates: MutableList<UpdatesModel>) : RecyclerView.Adapter<UpdateAdapter.UpdateViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpdateViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.update_recycler_viewholder, parent, false)
        return UpdateViewHolder(view)
    }

    override fun getItemCount(): Int = listOfUpdates.size

    override fun onBindViewHolder(holder: UpdateViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.update_viewholder_update_tv).text = listOfUpdates[position].updateTitle
        holder.itemView.findViewById<TextView>(R.id.update_viewholder_update_desc_tv).text = listOfUpdates[position].updateDesc


        holder.itemView.findViewById<TextView>(R.id.update_viewholder_update_event_tv).visibility = GONE
    }

    class UpdateViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)


}

class FullUpdateAdapter(val context: Context, val listOfUpdates: MutableList<FullUpdateModel>) : RecyclerView.Adapter<FullUpdateAdapter.UpdateViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FullUpdateAdapter.UpdateViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.update_recycler_viewholder, parent, false)
        return FullUpdateAdapter.UpdateViewHolder(view)
    }

    override fun getItemCount(): Int = listOfUpdates.size

    override fun onBindViewHolder(holder: FullUpdateAdapter.UpdateViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.update_viewholder_update_tv).text = listOfUpdates[position].updatesModel.updateTitle
        holder.itemView.findViewById<TextView>(R.id.update_viewholder_update_desc_tv).text = listOfUpdates[position].updatesModel.updateDesc


        holder.itemView.findViewById<TextView>(R.id.update_viewholder_update_event_tv).visibility = VISIBLE
        holder.itemView.findViewById<TextView>(R.id.update_viewholder_update_event_tv).text = listOfUpdates[position].eventName
    }

    class UpdateViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}