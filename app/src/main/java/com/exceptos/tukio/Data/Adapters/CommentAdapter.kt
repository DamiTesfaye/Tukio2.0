package com.exceptos.tukio.Data.Adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.exceptos.tukio.Data.ModelClasses.CommentModel
import com.exceptos.tukio.Data.ModelClasses.breakDownToUserModel
import com.exceptos.tukio.R
import com.exceptos.tukio.Repos.FireStoreRepo
import com.exceptos.tukio.Utils.DateFormatterUtil

/**
 * Created by Big-Nosed Developer on the Edge of Infinity.
 */
class CommentAdapter(val context: Context, val list: List<CommentModel>): RecyclerView.Adapter<com.exceptos.tukio.Data.Adapters.CommentAdapter.CommentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): com.exceptos.tukio.Data.Adapters.CommentAdapter.CommentViewHolder {
        val view =  LayoutInflater.from(context).inflate(R.layout.comment_layout, parent, false)
        return com.exceptos.tukio.Data.Adapters.CommentAdapter.CommentViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: com.exceptos.tukio.Data.Adapters.CommentAdapter.CommentViewHolder, position: Int) {
        val currentModel = list[position]

        val userNametv = holder.itemView.findViewById<TextView>(R.id.comment_layout_user_name)
        val commentTv = holder.itemView.findViewById<TextView>(R.id.comment_layout_comment)
        val userImage = holder.itemView.findViewById<ImageView>(R.id.comment_layout_user_image)
        val commentPostTime = holder.itemView.findViewById<TextView>(R.id.comment_layout_post_time)

        commentTv.text = currentModel.commentMessage

        FireStoreRepo().getUserAccountDetailsFromUid(currentModel.posterUID).addSnapshotListener {snapshot, _ ->
            if(snapshot != null && snapshot.exists()){

                val userModel = snapshot.breakDownToUserModel()
                userNametv.text = userModel.userName

                val requestOptions = RequestOptions()
                requestOptions.placeholder(ContextCompat.getDrawable(context, R.drawable.ic_male_placeholder))
                val thumbNailRequest = Glide.with(context.applicationContext).load(userModel.userThumbLink)

                Glide.with(context.applicationContext)
                    .setDefaultRequestOptions(requestOptions)
                    .asDrawable()
                    .load(userModel.userImage)
                    .thumbnail(thumbNailRequest)
                    .into(userImage)

            }
        }

        val finalDate = DateFormatterUtil.getRelativeTimeSpanString(context, currentModel.currentTimePosted).toString()
        commentPostTime.text = finalDate
    }

    class CommentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

}