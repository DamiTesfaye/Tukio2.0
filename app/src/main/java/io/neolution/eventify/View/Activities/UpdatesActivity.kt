package io.neolution.eventify.View.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageButton
import android.widget.LinearLayout
import com.google.firebase.firestore.CollectionReference
import io.neolution.eventify.Data.Adapters.UpdateAdapter
import io.neolution.eventify.Data.ModelClasses.UpdatesModel
import io.neolution.eventify.Data.ModelClasses.indicate
import io.neolution.eventify.Data.ModelClasses.toHashMap
import io.neolution.eventify.Listeners.OnUpdatesAdded
import io.neolution.eventify.R
import io.neolution.eventify.Repos.AuthRepo
import io.neolution.eventify.Repos.FireStoreRepo
import io.neolution.eventify.Utils.AppUtils
import io.neolution.eventify.View.CustomViews.UpdateDialogFragment

class UpdatesActivity : AppCompatActivity(), OnUpdatesAdded {

    private lateinit var particularCollectionReference: CollectionReference
    private lateinit var updateDialog: UpdateDialogFragment
    override fun onDoneBtnClicked(updateTitle: String, updateDescription: String) {

        updateDialog.dismiss()

        val dialog = AppUtils.instantiateProgressDialog("Adding Update", this)
        dialog.show()

        val updateModel = (UpdatesModel(updateTitle, updateDescription)).toHashMap()

        particularCollectionReference.add(updateModel).addOnCompleteListener {
            if (it.isSuccessful){
                dialog.dismiss()
            }else{
                dialog.dismiss()
                this.indicate("Error. Please check your internet connection")
            }
        }
    }

    private lateinit var list: MutableList<UpdatesModel>
    private lateinit var adapter: UpdateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_updates)

        list = mutableListOf()
        adapter = UpdateAdapter(this, list)

        findViewById<ImageButton>(R.id.activity_updates_close_imgbtn).setOnClickListener {
            onBackPressed()
        }

        val eventID = intent.getStringExtra("documentID")
        val posterID = intent.getStringExtra("posterID")

        particularCollectionReference = FireStoreRepo().getDocumentUpdatesPath(eventID)

        val noUpdateLayout = findViewById<LinearLayout>(R.id.activity_updates_no_update_layout)
        val recyclerView = findViewById<RecyclerView>(R.id.activity_updates_recycler)
        val fab = findViewById<FloatingActionButton>(R.id.activity_updates_fab)

        fab.visibility = if (AuthRepo.getUserUid() == posterID) VISIBLE  else GONE

        fab.setOnClickListener {
             updateDialog = UpdateDialogFragment()
            updateDialog.show(supportFragmentManager, "updateDialogFragment")
        }


        val linearLayoutManager = LinearLayoutManager(this)

        recyclerView.run {
            layoutManager = linearLayoutManager
            setHasFixedSize(true)
        }

        recyclerView.adapter = adapter

        particularCollectionReference.addSnapshotListener{ snapshot, _ ->

            if (snapshot != null && !snapshot.isEmpty){
                for (document in snapshot.documents){
                    val update = document.toObject(UpdatesModel::class.java)
                    list.add(update!!)
                    adapter.notifyDataSetChanged()
                }


            }else{
                recyclerView.visibility = GONE
                noUpdateLayout.visibility = VISIBLE
            }

        }


    }
}
