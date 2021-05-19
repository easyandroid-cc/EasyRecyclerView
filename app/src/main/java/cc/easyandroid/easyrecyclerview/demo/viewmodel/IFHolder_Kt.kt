package cc.easyandroid.easyrecyclerview.demo.viewmodel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import cc.easyandroid.easyrecyclerview.EasyFlexibleAdapter
import cc.easyandroid.easyrecyclerview.demo.R
import cc.easyandroid.easyrecyclerview.demo.data.RedditPost
import cc.easyandroid.easyrecyclerview.holders.FlexibleViewHolder
import cc.easyandroid.easyrecyclerview.items.IFlexible
import com.google.gson.annotations.SerializedName

class IFHolder_Kt(val redditPost: RedditPost) : IFlexible<IFHolder_Kt.ViewHolder?> {

    override fun isEnabled(): Boolean {
        return true
    }

    override fun setEnabled(enabled: Boolean) {}
    override fun isHidden(): Boolean {
        return false
    }

    override fun setHidden(hidden: Boolean) {}
    override fun isSelectable(): Boolean {
        return true
    }

    override fun setSelectable(selectable: Boolean) {}
    override fun getSpanSize(spanCount: Int, position: Int): Int {
        return 1
    }

    override fun getLayoutRes(): Int {
        return R.layout.reddit_post_item
    }

    override fun createViewHolder(adapter: EasyFlexibleAdapter<*>?, inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(layoutRes, parent, false), adapter)
    }


    inner class ViewHolder(view: View, adapter: EasyFlexibleAdapter<*>?) : FlexibleViewHolder(view, adapter) {
        private val title: TextView = view.findViewById(R.id.title)
        private val subtitle: TextView = view.findViewById(R.id.subtitle)
        private val score: TextView = view.findViewById(R.id.score)
        private val thumbnail: ImageView = view.findViewById(R.id.thumbnail)

        fun setData(post: RedditPost,position: Int) {

            title.text = post?.title ?: "loading"
            title.text = "position = $bindingAdapterPosition"
            subtitle.text = itemView.context.resources.getString(R.string.post_subtitle,
                    post?.author ?: "unknown")
            score.text = "${post?.score ?: 0}"
            if (post?.thumbnail?.startsWith("http") == true) {
                thumbnail.visibility = View.VISIBLE

//            glide.load(post.thumbnail)
//                    .centerCrop()
//                    .placeholder(R.drawable.ic_insert_photo_black_48dp)
//                    .into(thumbnail)
            } else {
                thumbnail.visibility = View.GONE
                //   glide.clear(thumbnail)
            }
        }

        override fun onClick(view: View) {
            super.onClick(view)
            println("cgp = count =${mAdapter.selectedItemCount}")
        }
    }

    override fun bindViewHolder(adapter: EasyFlexibleAdapter<out IFlexible<*>>?, holder: ViewHolder?, position: Int, payloads: MutableList<Any?>?) {
        // TODO("Not yet implemented")

//        Toast.makeText(holder..context, "$position", Toast.LENGTH_LONG).show()
        holder!!.setData(redditPost,position);
    }
}