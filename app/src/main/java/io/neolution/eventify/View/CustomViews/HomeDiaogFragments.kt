package io.neolution.eventify.View.CustomViews

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import io.neolution.eventify.R

class HomeDialogFragment: DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.home_dialog_fragment, container, false)

        val button = view.findViewById<Button>(R.id.home_dialog_frag_close)
        button.setOnClickListener {
            dismiss()
        }

        return view
    }

}

class ExploreDialogFragment: DialogFragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.explore_dialog_fragment, container, false)

        val button = view.findViewById<Button>(R.id.explore_dialog_frag_close)
        button.setOnClickListener {
            dismiss()
        }

        return view
    }
}

class UpdateHomeDialogFragment: DialogFragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.update_home_dialog_fragment, container, false)

        val button = view.findViewById<Button>(R.id.update_home_dialog_frag_close)
        button.setOnClickListener {
            dismiss()
        }

        return view
    }

}