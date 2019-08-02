package io.neolution.eventify.Data.Adapters

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import io.neolution.eventify.Data.ModelClasses.ChipModel
import io.neolution.eventify.Listeners.OnChipSelected
import io.neolution.eventify.R

/**
 * Created by Big-Nosed Developer on the Edge of Infinity.
 */
class AddTagsAdapter(var chipList: MutableList<ChipModel>, var context: Context, val onChipSelected__ : OnChipSelected): RecyclerView.Adapter<AddTagsAdapter.TagViewHolder>() {

    lateinit var view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
         view= LayoutInflater.from(context).inflate(R.layout.chip_layout, parent, false)
        return TagViewHolder(view)
    }

    override fun getItemCount(): Int {
        return chipList.size
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {

        val chipContainer = holder.itemView.findViewById<RelativeLayout>(R.id.chip_layout_container)
        val chipTextView = holder.itemView.findViewById<TextView>(R.id.chip_layout_text)
        val chipImageView = holder.itemView.findViewById<ImageView>(R.id.chip_layout_image)

        val chipText = chipList[position].chipText
        val chipImage = chipList[position].imageResource

        chipTextView.text = chipText
        chipImageView.setImageDrawable(ContextCompat.getDrawable(context, chipImage))

        chipContainer.setOnClickListener {

            onChipSelected__.onChipSelected(chipTextView.text.toString())
        }
    }

    fun getViewHolder() = TagViewHolder(view)


    inner class TagViewHolder(v: View): RecyclerView.ViewHolder(v)


}