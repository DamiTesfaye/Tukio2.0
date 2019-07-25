package io.neolution.eventify.View.CustomViews

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import io.neolution.eventify.R
import io.neolution.eventify.Repos.CardDetailsRepo

class PromotedDialog: DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.promoted_dialog_fragment, container, false)
        val alrightButton = view.findViewById<Button>(R.id.promoted_dialog_alright)

        alrightButton.setOnClickListener {
            val doneSaving = CardDetailsRepo(context!!).firstTimePromoting()
            if (doneSaving){
                dialog.dismiss()
            }
        }
        return view

    }

}