package io.neolution.eventify.Data.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.neolution.eventify.Data.ModelClasses.FullEventsModel
import io.neolution.eventify.R

class ExploreAdapter(val context: Context, val eventList: List<FullEventsModel>): RecyclerView.Adapter<ExploreAdapter.ExploreViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExploreViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.explore_event_vh, parent, false)
        return ExploreViewHolder(view)
    }

    override fun getItemCount() = eventList.size

    override fun onBindViewHolder(holder: ExploreViewHolder, position: Int) {

    }

    class ExploreViewHolder(val v: View): RecyclerView.ViewHolder(v)

}