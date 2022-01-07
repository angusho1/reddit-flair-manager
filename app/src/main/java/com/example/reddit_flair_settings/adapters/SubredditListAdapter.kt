package com.example.reddit_flair_settings.adapters

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reddit_flair_settings.R
import com.example.reddit_flair_settings.models.UserSubreddit
import com.example.reddit_flair_settings.utils.InternetImageLoader
import kotlinx.android.synthetic.main.subreddit_item.view.*
import java.lang.Exception
import java.net.URL

// Ref: https://github.com/philipplackner/TodoList/blob/master/app/src/main/java/com/example/todolist/TodoAdapter.kt#L20

// Ref: https://www.geeksforgeeks.org/how-to-create-a-nested-recyclerview-in-android/

class SubredditListAdapter(
    private val subreddits: MutableList<UserSubreddit>
): RecyclerView.Adapter<SubredditListAdapter.SubredditViewHolder>() {
    private val viewPool = RecyclerView.RecycledViewPool()

    class SubredditViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val childRecyclerView: RecyclerView = itemView.findViewById(R.id.rv_flair)
    }

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
        val flair = currSubreddit.flair

        holder.itemView.apply {
            rv_flair.setBackgroundColor(flair.backgroundColor)
            subredditName.text = currSubreddit.name

            if (currSubreddit.iconUrl != null) {
                InternetImageLoader(subredditIcon).execute(currSubreddit.iconUrl.toString())
            }
        }

        if (!currSubreddit.flairsEnabled) {
            return
        }

        val layoutManager = LinearLayoutManager(
            holder.childRecyclerView.context,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        if (currSubreddit.flair.richText != null) {
            layoutManager.initialPrefetchItemCount = currSubreddit.flair.richText.size
        } else {
            layoutManager.initialPrefetchItemCount = 1
        }

        val flairAdapter = FlairAdapter(currSubreddit.flair)

        holder.childRecyclerView.layoutManager = layoutManager
        holder.childRecyclerView.adapter = flairAdapter
        holder.childRecyclerView.setRecycledViewPool(viewPool)
    }

    override fun getItemCount(): Int {
        return subreddits.size
    }
}
