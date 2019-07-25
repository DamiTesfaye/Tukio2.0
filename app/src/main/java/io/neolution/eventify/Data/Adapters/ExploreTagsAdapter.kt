package io.neolution.eventify.Data.Adapters

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.robertlevonyan.views.chip.Chip
import io.neolution.eventify.R
import io.neolution.eventify.View.Activities.HomeActivity
import io.neolution.eventify.View.Activities.PostedEventsActivity
import io.neolution.eventify.View.Activities.PromotedEventsActivity
import io.neolution.eventify.View.Activities.TagActivity

/**
 * Created by Big-Nosed Developer on the Edge of Infinity.
 */
class ExploreTagsAdapter(var stringList: List<String>, var context: Context, val activity: FragmentActivity): RecyclerView.Adapter<ExploreTagsAdapter.TagViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.explore_chip_layout, parent, false)
        return TagViewHolder(view)
    }

    override fun getItemCount(): Int {
        return stringList.size
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val chip = holder.itemView as Chip


        chip.apply {
            chipText = stringList[position]
        }

        chip.setOnClickListener {
            if (context is HomeActivity || context is PostedEventsActivity || context is PromotedEventsActivity){
                val intent = Intent(context, TagActivity::class.java)
                intent.putExtra("title", stringList[position])

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle())
                }else{
                    context.startActivity(intent)
                }
            }

        }
    }
    inner class TagViewHolder(v: View): RecyclerView.ViewHolder(v)
}