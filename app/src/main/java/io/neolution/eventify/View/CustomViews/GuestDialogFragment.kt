package io.neolution.eventify.View.CustomViews

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import io.neolution.eventify.Listeners.OnGuestAddedListener
import io.neolution.eventify.R
import kotlinx.android.synthetic.main.guest_dialog_fragment.*
import java.lang.ClassCastException

/**
 * Created by Big-Nosed Developer on the Edge of Infinity.
 */
class GuestDialogFragment: DialogFragment() {

    lateinit var listener: OnGuestAddedListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.guest_dialog_fragment, container, false)

        val addButton = view.findViewById<Button>(R.id.guests_dialog_add_button)
        addButton.setOnClickListener {
            if (!guests_dialog_guest_name_et.text.isNullOrEmpty() && !guests_dialog_guest_nbio_et.text.isNullOrEmpty()){

                //Triggering said listener..
                listener.onGuestAdded(guests_dialog_guest_name_et.text.toString(), guests_dialog_guest_nbio_et.text.toString())

            }else {
                Toast.makeText(context, "Please add a guests name", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        try{
            listener = context as OnGuestAddedListener
        }catch (e: ClassCastException){
            e.printStackTrace()
        }


    }

    override fun onStart() {
        super.onStart()

        val dialog = dialog
        dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
    }

}