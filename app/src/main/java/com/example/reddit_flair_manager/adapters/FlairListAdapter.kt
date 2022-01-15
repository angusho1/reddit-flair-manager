package com.example.reddit_flair_manager.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reddit_flair_manager.R
import com.example.reddit_flair_manager.models.UserFlair
import com.example.reddit_flair_manager.models.UserSubreddit
import com.example.reddit_flair_manager.utils.InternetImageLoader
import kotlinx.android.synthetic.main.subreddit_item.view.*

class FlairListAdapter(
    private val flairOptions: MutableList<UserFlair>
): RecyclerView.Adapter<FlairListAdapter.FlairOptionViewHolder>() {
    private val viewPool = RecyclerView.RecycledViewPool()

    class FlairOptionViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val childRecyclerView: RecyclerView = itemView.findViewById(R.id.rv_flair)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlairOptionViewHolder {
        return FlairOptionViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.flair_option,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FlairOptionViewHolder, position: Int) {
        val currFlairOption = flairOptions[position]

        holder.itemView.apply {
            rv_flair.setBackgroundColor(currFlairOption.backgroundColor)
        }

        val layoutManager = LinearLayoutManager(
            holder.childRecyclerView.context,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        if (currFlairOption.richText != null) {
            layoutManager.initialPrefetchItemCount = currFlairOption.richText.size
        } else {
            layoutManager.initialPrefetchItemCount = 1
        }

        val flairAdapter = FlairAdapter(currFlairOption)

        holder.childRecyclerView.layoutManager = layoutManager
        holder.childRecyclerView.adapter = flairAdapter
        holder.childRecyclerView.setRecycledViewPool(viewPool)
    }

    override fun getItemCount(): Int {
        return flairOptions.size
    }
}
