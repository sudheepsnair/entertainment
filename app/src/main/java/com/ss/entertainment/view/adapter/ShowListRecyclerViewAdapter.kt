package com.ss.entertainment.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ss.entertainment.R
import com.ss.entertainment.model.Season
import com.ss.entertainment.view.ui.ShowListFragment
import kotlinx.android.synthetic.main.item_show_layout.view.*


class ShowListRecyclerViewAdapter(
    private val mListener: ShowListFragment.OnListFragmentInteractionListener?
) : RecyclerView.Adapter<ShowListRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener
    private var mValues: List<Season>? = null

    init {

        mOnClickListener = View.OnClickListener { v ->
            val season = v.tag as Season
            mListener?.onListFragmentInteraction(season)
        }
    }

    fun setData(season: List<Season>) {
        mValues = season
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_show_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val season = mValues?.get(position)

        holder.mContentView.text = season?.title ?: "Title Show"

        with(holder.mView) {
            tag = season
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues?.size ?: 0

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mContentView: TextView = mView.showTitle

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }
}
