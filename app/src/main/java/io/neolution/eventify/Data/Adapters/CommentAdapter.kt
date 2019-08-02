package io.neolution.eventify.Data.Adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import io.neolution.eventify.Data.ModelClasses.CommentModel
import io.neolution.eventify.Data.ModelClasses.breakDownToUserModel
import io.neolution.eventify.R
import io.neolution.eventify.Repos.FireStoreRepo
import io.neolution.eventify.Utils.DateFormatterUtil

/**
 * Created by Big-Nosed Developer on the Edge of Infinity.
 */
class CommentAdapter(val context: Context, val list: List<CommentModel>): RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view =  LayoutInflater.from(context).inflate(R.layout.comment_layout, parent, false)
        return CommentViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val currentModel = list[position]

        val userNametv = holder.itemView.findViewById<TextView>(R.id.comment_layout_user_name)
        val commentTv = holder.itemView.findViewById<TextView>(R.id.comment_layout_comment)
        val userImage = holder.itemView.findViewById<CircleImageView>(R.id.comment_layout_user_image)
        val commentPostTime = holder.itemView.findViewById<TextView>(R.id.comment_layout_post_time)

        commentTv.text = currentModel.commentMessage

        FireStoreRepo().getUserAccountDetailsFromUid(currentModel.posterUID).addSnapshotListener {snapshot, _ ->
            if(snapshot != null && snapshot.exists()){

                val userModel = snapshot.breakDownToUserModel()
                userNametv.text = userModel.userName

                Glide.with(context.applicationContext)
                    .asDrawable()
                    .load(userModel.userImage)
                    .into(userImage)

            }
        }

        val finalDate = DateFormatterUtil.getRelativeTimeSpanString(context, currentModel.currentTimePosted).toString()
        commentPostTime.text = finalDate
    }

    class CommentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

}