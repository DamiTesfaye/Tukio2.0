package com.exceptos.tukio.Data.Adapters

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.exceptos.tukio.Data.ModelClasses.ChipModel
import com.exceptos.tukio.Listeners.OnChipSelected
import com.exceptos.tukio.R

/**
 * Created by Big-Nosed Developer on the Edge of Infinity.
 */
class TagsAdapter(var chipList: MutableList<ChipModel>, var context: Context, val alreadyStoredTags: List<String>?, val onChipSelected__ : OnChipSelected): RecyclerView.Adapter<com.exceptos.tukio.Data.Adapters.TagsAdapter.TagViewHolder>() {

    lateinit var view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): com.exceptos.tukio.Data.Adapters.TagsAdapter.TagViewHolder {
         view= LayoutInflater.from(context).inflate(R.layout.chip_layout, parent, false)
        return TagViewHolder(view)
    }

    override fun getItemCount(): Int {
        return chipList.size
    }

    override fun onBindViewHolder(holder: com.exceptos.tukio.Data.Adapters.TagsAdapter.TagViewHolder, position: Int) {
        var isSelected = false

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

                onChipSelected__.onChipSelected(chipTextView.text.toString())

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

        chipContainer.setOnClickListener {
            if (isSelected){

                chipContainer.background = ContextCompat.getDrawable(context, R.drawable.buttonbg_outline)
                chipTextView.setTextColor(context.resources.getColor(android.R.color.black))

                onChipSelected__.onChipDeselected(chipTextView.text.toString())
                isSelected = false

            }else{
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