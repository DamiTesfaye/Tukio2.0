package io.neolution.eventify.View.CustomViews

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import io.neolution.eventify.Listeners.OnGuestAddedListener
import io.neolution.eventify.Listeners.OnNameBioInputted
import io.neolution.eventify.R
import java.lang.ClassCastException

class NameBioDialog: DialogFragment() {
    lateinit var onNameBioInputted: OnNameBioInputted

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.name_bio_dialog_layout, container, false)

        val bioEt = view.findViewById<EditText>(R.id.name_bio_name_et)
        val nameEt = view.findViewById<EditText>(R.id.name_bio_bio_et)
        val saveBtn = view.findViewById<Button>(R.id.name_bio_save_button)

        if (arguments != null){
            bioEt.setText(arguments!!.getString("userBio"))
            nameEt.setText(arguments!!.getString("userName"))
        }

        saveBtn.setOnClickListener {
            if (!nameEt.text.toString().isEmpty() && !bioEt.text.toString().isEmpty())
            onNameBioInputted.onNameBioDone(name = nameEt.text.toString(), bio = bioEt.text.toString())
        }

        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        try{
            onNameBioInputted = context as OnNameBioInputted
        }catch (e: ClassCastException){
            e.printStackTrace()
        }

    }

    override fun onStart() {
        super.onStart()

        val dialog = dialog
        dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}