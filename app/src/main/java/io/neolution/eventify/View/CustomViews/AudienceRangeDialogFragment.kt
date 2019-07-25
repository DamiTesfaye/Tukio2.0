package io.neolution.eventify.View.CustomViews

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import io.neolution.eventify.Listeners.OnAudienceRangeSelected
import io.neolution.eventify.R

/**
 * Created by Big-Nosed Developer on the Edge of Infinity.
 */
class AudienceRangeDialogFragment : DialogFragment(), View.OnClickListener {

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.low_range_layout -> {
                audienceRangeSelected.onAudienceRangeSelected(100000)
            }
            R.id.high_range_layout -> {
                audienceRangeSelected.onAudienceRangeSelected(250000)
            }
            R.id.medium_range_layour -> {
                audienceRangeSelected.onAudienceRangeSelected(500000)
            }
        }
    }

    lateinit var audienceRangeSelected: OnAudienceRangeSelected

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.choose_audience_range_dialog_fragment, container, false)

        val lowRangeLayout = view.findViewById<LinearLayout>(R.id.low_range_layout)
        val mediumRangeLayour = view.findViewById<LinearLayout>(R.id.high_range_layout)
        val highRangeLayout = view.findViewById<LinearLayout>(R.id.high_range_layout)

        lowRangeLayout.setOnClickListener(this)
        mediumRangeLayour.setOnClickListener(this)
        highRangeLayout.setOnClickListener(this)

        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        audienceRangeSelected = context as OnAudienceRangeSelected
    }

}