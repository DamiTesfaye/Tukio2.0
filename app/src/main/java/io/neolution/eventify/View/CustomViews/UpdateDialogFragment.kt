package io.neolution.eventify.View.CustomViews

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import io.neolution.eventify.Data.ModelClasses.indicate
import io.neolution.eventify.Listeners.OnUpdatesAdded
import io.neolution.eventify.R

/**
 * Created by Big-Nosed Developer on the Edge of Infinity.
 */
class UpdateDialogFragment: DialogFragment() {

    lateinit var OnUpdatesAdded: OnUpdatesAdded

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.update_dialog_fragment, container, false)

        val updateTiteET = view.findViewById<EditText>(R.id.update_dialog_frag_title)
        val updateDescET = view.findViewById<EditText>(R.id.update_dialog_frag_description)
        val updateDoneButton = view.findViewById<Button>(R.id.update_dialog_frag_done_btn)

        updateDoneButton.setOnClickListener {

            if (!updateDescET.text.toString().isEmpty() && !updateTiteET.text.toString().isEmpty()){
                OnUpdatesAdded.onDoneBtnClicked(updateTitle = updateTiteET.text.toString(),
                    updateDescription = updateDescET.text.toString())
            }else{
                context!!.indicate("Please input all fields")
            }

        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        OnUpdatesAdded = (context as OnUpdatesAdded)
    }

    override fun onStart() {
        super.onStart()

        val dialog = dialog
        dialog!!.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog!!.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

}