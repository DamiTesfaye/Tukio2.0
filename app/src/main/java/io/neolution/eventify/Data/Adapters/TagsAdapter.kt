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
class TagsAdapter(var chipList: MutableList<ChipModel>, var context: Context, val alreadyStoredTags: List<String>?, val onChipSelected__ : OnChipSelected): RecyclerView.Adapter<TagsAdapter.TagViewHolder>() {

    lateinit var view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
         view= LayoutInflater.from(context).inflate(R.layout.chip_layout, parent, false)
        return TagViewHolder(view)
    }

    override fun getItemCount(): Int {
        return chipList.size
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        var isSelected: Boolean

        val chipContainer = holder.itemView.findViewById<RelativeLayout>(R.id.chip_layout_container)
        val chipTextView = holder.itemView.findViewById<TextView>(R.id.chip_layout_text)
        val chipImageView = holder.itemView.findViewById<ImageView>(R.id.chip_layout_image)

        val chipText = chipList[position].chipText
        val chipImage = chipList[position].imageResource

        chipTextView.text = chipText
        chipImageView.setImageDrawable(ContextCompat.getDrawable(context, chipImage))

        if(alreadyStoredTags != null && !alreadyStoredTags.isEmpty()) {

            if (alreadyStoredTags.contains(chipTextView.text.toString())) {

                isSelected = true

                chipContainer.background = ContextCompat.getDrawable(context, R.drawable.buttonbg)
                chipTextView.setTextColor(context.resources.getColor(R.color.colorPrimary))

            }else{
                isSelected = false

                chipContainer.background = ContextCompat.getDrawable(context, R.drawable.buttonbg_outline)
                chipTextView.setTextColor(context.resources.getColor(android.R.color.black))

            }

        }else{
            isSelected = false

            chipContainer.background = ContextCompat.getDrawable(context, R.drawable.buttonbg_outline)
            chipTextView.setTextColor(context.resources.getColor(android.R.color.black))
        }


        if (isSelected){

            chipContainer.setOnClickListener {
                chipContainer.background = ContextCompat.getDrawable(context, R.drawable.buttonbg_outline)
                chipTextView.setTextColor(context.resources.getColor(android.R.color.black))

                onChipSelected__.onChipDeselected(chipTextView.text.toString())
                isSelected = false
            }
        }else{
            chipContainer.setOnClickListener {
                chipContainer.background = ContextCompat.getDrawable(context, R.drawable.buttonbg)
                chipTextView.setTextColor(context.resources.getColor(R.color.colorPrimary))

                onChipSelected__.onChipSelected(chipTextView.text.toString())
                isSelected = true
            }
        }
    }

    fun getViewHolder() = TagViewHolder(view)


    inner class TagViewHolder(v: View): RecyclerView.ViewHolder(v)


}