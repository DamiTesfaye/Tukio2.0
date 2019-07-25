package io.neolution.eventify.View.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.google.firebase.firestore.DocumentChange
import io.neolution.eventify.Data.Adapters.CommentAdapter
import io.neolution.eventify.Data.ModelClasses.CommentModel
import io.neolution.eventify.Data.ModelClasses.breakDownToCommentModel
import io.neolution.eventify.Data.ModelClasses.toHashMap
import io.neolution.eventify.R
import io.neolution.eventify.Repos.AuthRepo
import io.neolution.eventify.Repos.FireStoreRepo
import io.neolution.eventify.Utils.AppUtils

class CommentActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.comment_activity_send_message_button -> {
               if (!commentEditText.text.toString().isNullOrEmpty() && commentEditText.text.toString().isNotBlank()){
                    sendComment( commentEditText.text.toString())
               }
            }

            R.id.comment_activity_close -> {
                onBackPressed()
            }
        }
    }

    lateinit var commentEditText: EditText
    lateinit var commentRecyclerView: RecyclerView
    lateinit var progressBar: ProgressBar
    lateinit var noCommentLayout: LinearLayout
    lateinit var eventID: String

    lateinit var adapter: CommentAdapter
    lateinit var list: MutableList<CommentModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

         eventID = intent.getStringExtra("eventID")

        commentRecyclerView = findViewById(R.id.comment_activity_recycler)

        list = mutableListOf()
        adapter = CommentAdapter(this, list)

        val commentSend = findViewById<FloatingActionButton>(R.id.comment_activity_send_message_button)
        val closeActivity = findViewById<ImageButton>(R.id.comment_activity_close)

        commentEditText = findViewById(R.id.comment_activity_message_edit_text)
        progressBar = findViewById(R.id.comment_activity_progress_bar)
        noCommentLayout = findViewById(R.id.no_comments_layout)

        progressBar.visibility = VISIBLE

        val manager = AppUtils.getRecycleLayout(AppUtils.LINEAR_LAYOUT_MANAGER, this)
        commentRecyclerView.run {
            setHasFixedSize(true)
            layoutManager = manager
        }

        commentRecyclerView.adapter = adapter

        commentSend.setOnClickListener(this)
        closeActivity.setOnClickListener(this)

        loadComments()
    }

    private fun sendComment(comment: String){

        commentEditText.setText("")

        val uID = AuthRepo.getUserUid()
        val currentPostTime = System.currentTimeMillis()

        val commentModel = CommentModel(currentTimePosted = currentPostTime, commentMessage = comment, posterUID = uID)
        val hashMap = commentModel.toHashMap()

        FireStoreRepo().getDocumentCommentCollection(eventID).add(hashMap)

    }

    private fun loadComments() {

        FireStoreRepo().getDocumentCommentCollection(eventID).orderBy("currentTimePosted").addSnapshotListener { snapshot, _ ->
            if (snapshot != null && !snapshot.isEmpty){
                for (doc in snapshot.documentChanges){
                    if (doc.type == DocumentChange.Type.ADDED){

                        val model = doc.document.breakDownToCommentModel()
                        if (!list.contains(model)) {
                            list.add(model)
                            adapter.notifyDataSetChanged()

                        }
                    }

                }

                progressBar.visibility = GONE
                noCommentLayout.visibility = GONE
                commentRecyclerView.visibility = VISIBLE

            }else{
                progressBar.visibility = GONE
                noCommentLayout.visibility = VISIBLE
                commentRecyclerView.visibility = GONE
            }
        }
    }


}
