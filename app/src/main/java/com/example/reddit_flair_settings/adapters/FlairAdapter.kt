package com.example.reddit_flair_settings.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.reddit_flair_settings.R
import com.example.reddit_flair_settings.models.*
import com.example.reddit_flair_settings.utils.InternetImageLoader
import kotlinx.android.synthetic.main.flair_image.view.*
import kotlinx.android.synthetic.main.flair_text.view.*

class FlairAdapter(
    private val flair: UserFlair
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class FlairTextViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    class FlairImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder = when (viewType) {
            VIEW_TYPE_TEXT -> FlairTextViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.flair_text,
                    parent,
                    false
                )
            )
            VIEW_TYPE_IMAGE -> FlairImageViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.flair_image,
                    parent,
                    false
                )
            )
            else -> FlairTextViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.flair_text,
                    parent,
                    false
                )
            )
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val component: FlairRichTextComponent = if (flair.richText != null) {
            flair.richText[position]
        } else {
            TextComponent("")
        }

        when (holder.itemViewType) {
            VIEW_TYPE_TEXT -> {
                val viewHolder = holder as FlairTextViewHolder
                val textComponent = component as TextComponent
                viewHolder.itemView.apply {
                    flairText.text = textComponent.text
                    flairText.setTextColor(flair.textColor)
                }
            }
            VIEW_TYPE_IMAGE -> {
                val viewHolder = holder as FlairImageViewHolder
                val iconComponent = component as IconComponent
                viewHolder.itemView.apply {
                    InternetImageLoader(flairImage).execute(iconComponent.url)
                }
            }
        }

        // https://stackoverflow.com/questions/26245139/how-to-create-recyclerview-with-multiple-view-types/26245463#26245463

        // https://stackoverflow.com/questions/34446302/how-to-add-a-dynamic-view-in-between-items-of-recyclerview-in-android
    }

    override fun getItemCount(): Int {
        return if (flair.type == FLAIR_TYPE_RICH_TEXT) {
            (flair.richText as List<FlairRichTextComponent>).size
        } else {
            1
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (flair.type == FLAIR_TYPE_RICH_TEXT) {
            val richText = flair.richText as List<FlairRichTextComponent>
            richText[position].getViewType()
        } else {
            VIEW_TYPE_TEXT
        }
    }
}