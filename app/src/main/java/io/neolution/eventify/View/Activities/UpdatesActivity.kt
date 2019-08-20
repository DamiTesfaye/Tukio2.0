package io.neolution.eventify.View.Activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.CollectionReference
import io.neolution.eventify.Data.Adapters.UpdateAdapter
import io.neolution.eventify.Data.ModelClasses.UpdatesModel
import io.neolution.eventify.Data.ModelClasses.indicate
import io.neolution.eventify.Data.ModelClasses.toHashMap
import io.neolution.eventify.R
import io.neolution.eventify.Repos.AuthRepo
import io.neolution.eventify.Repos.FireStoreRepo
import kotlinx.android.synthetic.main.activity_updates.*

class UpdatesActivity : AppCompatActivity() {

    private lateinit var particularCollectionReference: CollectionReference
    private lateinit var list: MutableList<UpdatesModel>
    private lateinit var adapter: UpdateAdapter

    private lateinit var addUpdateBottomSheet: LinearLayout
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var closeBottomSheet: ImageButton
    private lateinit var addUpdateButton: TextView
    private lateinit var addUpdateLayout: RelativeLayout
    private lateinit var addUpdateProgressBar: ProgressBar

    private lateinit var updateTitleLayout: TextInputLayout
    private lateinit var updateTitleText: TextInputEditText
    private lateinit var updateDescLayout: TextInputLayout
    private lateinit var updateDescText: TextInputEditText
    private lateinit var updateProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_updates)

        list = mutableListOf()
        adapter = UpdateAdapter(this, list)

        findViewById<ImageButton>(R.id.activity_updates_close_imgbtn).setOnClickListener {
            onBackPressed()
        }

        addUpdateBottomSheet = findViewById(R.id.activity_update_bottomsheet)
        updateProgressBar = findViewById(R.id.activity_update_progress)
        bottomSheetBehavior = BottomSheetBehavior.from(addUpdateBottomSheet)
        closeBottomSheet = findViewById(R.id.add_update_bsheet_close)
        addUpdateButton = findViewById(R.id.add_update_bsheet_add_guest_text)
        addUpdateLayout = findViewById(R.id.add_update_bsheet_add_guest_layout)
        addUpdateProgressBar = findViewById(R.id.add_update_bsheet_add_guest_progress_bar)

        updateTitleLayout = findViewById(R.id.add_update_bsheet_guest_name_layout)
        updateTitleText = findViewById(R.id.add_update_bsheet_guest_name_edit)
        updateDescLayout = findViewById(R.id.add_update_bsheet_guest_desc_layout)
        updateDescText = findViewById(R.id.add_update_bsheet_guest_desc_edit)

        val eventID = intent.getStringExtra("documentID")
        val posterID = intent.getStringExtra("posterID")

        particularCollectionReference = FireStoreRepo().getDocumentUpdatesPath(eventID!!)

        val noUpdateLayout = findViewById<LinearLayout>(R.id.activity_updates_no_update_layout)
        val recyclerView = findViewById<RecyclerView>(R.id.activity_updates_recycler)
        val fab = findViewById<FloatingActionButton>(R.id.activity_updates_fab)

        fab.visibility = if (AuthRepo.getUserUid() == posterID) VISIBLE  else GONE

        fab.setOnClickListener {
             bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            activity_updates_bottom_sheet_bg.visibility = VISIBLE
        }

        closeBottomSheet.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            activity_updates_bottom_sheet_bg.visibility = GONE
        }

        activity_updates_bottom_sheet_bg.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            activity_updates_bottom_sheet_bg.visibility = GONE
        }

        addUpdateButton.setOnClickListener {
            val updateTitle = updateTitleText.text.toString().trim()
            val updateDesc = updateDescText.text.toString().trim()

            if (updateTitle.isNotEmpty() && updateDesc.isNotEmpty()){
                closeKeyboard()
                addUpdateLayout.background = ContextCompat.getDrawable(this, R.drawable.buttonbg_outline)
                addUpdateButton.visibility = GONE
                addUpdateProgressBar.visibility = VISIBLE

                val updateModel = (UpdatesModel(updateTitle, updateDesc)).toHashMap()
                particularCollectionReference.add(updateModel).addOnCompleteListener {
                    if (it.isSuccessful){

                        this.indicate("Update Sent")
                        updateTitleText.setText("")
                        updateDescText.setText("")

                        addUpdateLayout.background = ContextCompat.getDrawable(this, R.drawable.buttonbg)
                        addUpdateButton.visibility = VISIBLE
                        addUpdateProgressBar.visibility = GONE

                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        activity_updates_bottom_sheet_bg.visibility = GONE

                    }else{

                        addUpdateLayout.background = ContextCompat.getDrawable(this, R.drawable.buttonbg)
                        addUpdateButton.visibility = VISIBLE
                        addUpdateProgressBar.visibility = GONE

                        this.indicate("Error. Please check your internet connection")
                    }
                }

            }else{
                if (updateTitle.isEmpty()){
                    updateTitleLayout.isErrorEnabled = true
                    updateTitleLayout.error = "Please input a title for your update"
                }else if (updateDesc.isEmpty()){
                    updateDescLayout.isErrorEnabled = true
                    updateDescLayout.error = "Please input a title for your update"
                }
            }
        }


        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.run {
            layoutManager = linearLayoutManager
            setHasFixedSize(true)
        }

        recyclerView.adapter = adapter

        particularCollectionReference.addSnapshotListener(this){ snapshot, _ ->

            if (snapshot != null && !snapshot.isEmpty){
                for (document in snapshot.documents){
                    val update = document.toObject(UpdatesModel::class.java)
                    updateProgressBar.visibility = GONE
                    list.add(update!!)
                    adapter.notifyDataSetChanged()
                }


            }else{
                updateProgressBar.visibility = GONE
                recyclerView.visibility = GONE
                noUpdateLayout.visibility = VISIBLE
            }

        }


    }

    private fun closeKeyboard(){
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = currentFocus
        if (view == null){
            view = View(this)
        }

        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
