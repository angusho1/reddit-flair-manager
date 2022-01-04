package com.example.reddit_flair_settings

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.reddit_flair_settings.models.UserSubreddit
import kotlinx.android.synthetic.main.subreddit_item.view.*
import java.io.IOException
import java.net.URL

// Ref: https://github.com/philipplackner/TodoList/blob/master/app/src/main/java/com/example/todolist/TodoAdapter.kt#L20

class SubredditListAdapter(
    private val subreddits: MutableList<UserSubreddit>
): RecyclerView.Adapter<SubredditListAdapter.SubredditViewHolder>() {

    class SubredditViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubredditViewHolder {
        return SubredditViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.subreddit_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SubredditViewHolder, position: Int) {
        val currSubreddit = subreddits[position]
        holder.itemView.apply {
            subredditName.text = currSubreddit.name

            if (currSubreddit.iconUrl != null) {
//                val inputStream = currSubreddit.iconUrl.openStream()
//                val decodedStream = BitmapFactory.decodeStream(inputStream)
//
//                subredditIcon.setImageBitmap(decodedStream)
            }

//            val iconBitmap: Deferred<Bitmap?> = lifecycleScope.async(Dispatchers.IO) {
//                currSubreddit.iconUri.toBitmap
//            }
//
//            LifecycleCoroutineScope.launch(Dispatchers.Main) {
//                subredditIcon.setImageBitmap(iconBitmap.await())
//            }
        }
    }

    override fun getItemCount(): Int {
        return subreddits.size
    }
}

val URL.toBitmap:Bitmap?
    get() {
        return try {
            BitmapFactory.decodeStream(openStream())
        } catch (e: IOException) { null }
    }