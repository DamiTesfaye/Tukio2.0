package io.neolution.eventify.Data.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.robertlevonyan.views.chip.Chip
import io.neolution.eventify.Listeners.OnChipSelected
import io.neolution.eventify.R

/**
 * Created by Big-Nosed Developer on the Edge of Infinity.
 */
class TagsDialogAdapter(var stringList: MutableList<String>, var context: Context, val onChipSelected__: OnChipSelected): RecyclerView.Adapter<TagsDialogAdapter.TagViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.tags_dialog_viewholder, parent, false)
        return TagViewHolder(view)
    }

    override fun getItemCount(): Int {
        return stringList.size
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val chip = holder.itemView as Chip

        chip.apply {
            chipText = stringList[position]
            setOnSelectClickListener { _, selected ->
                if(selected){
                    onChipSelected__.onChipSelected(chipText)
                }else{
                    onChipSelected__.onChipDeselected(chipText)
                }
            }
        }


    }
    inner class TagViewHolder(v: View): RecyclerView.ViewHolder(v)
}