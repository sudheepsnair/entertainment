package com.ss.entertainment.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ss.entertainment.R
import com.ss.entertainment.model.Episode
import com.ss.entertainment.model.Season
import com.ss.entertainment.view.ui.EpisodesFragment
import com.ss.entertainment.view.viewholder.BaseViewHolder
import kotlinx.android.synthetic.main.item_episode_layout.view.*

class EpisodesRecyclerViewAdapter(
    private val mListener: EpisodesFragment.OnEpisodeFragmentInteractionListener?, season: Season?
) : RecyclerView.Adapter<EpisodesRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener
    private var mValues: List<Episode>? = season?.episodes


    init {
        mOnClickListener = View.OnClickListener { v ->
            val episode = v.tag as Episode
            mListener?.onEpisodeFragmentInteraction(episode)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_episode_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int = mValues?.size ?: 0

    inner class ViewHolder(private val view: View) : BaseViewHolder(view) {

        private val mContentView: TextView = view.textViewTitle
        val mThumbnail: ImageView? = view.thumbnail
        val mProgressBar: ProgressBar? = view.progressBar
        val mVideoLayout: FrameLayout? = view.video_layout

        override fun clear() {

        }

        override fun onBind(position: Int) {
            super.onBind(position)
            view.tag = this
            val episode = mValues?.get(position)

            mContentView.text = episode?.title ?: "Title"

            Glide.with(mThumbnail?.context)
                .load(episode?.thumbnail?.originalUrl).apply(RequestOptions().optionalCenterCrop())
                .into(mThumbnail)

            with(view) {
                setOnClickListener(mOnClickListener)
            }
        }
    }
}
