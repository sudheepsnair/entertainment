package com.ss.entertainment.view.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.arch.lifecycle.ViewModelProviders

import android.arch.lifecycle.Observer
import com.ss.entertainment.R
import com.ss.entertainment.model.Season
import com.ss.entertainment.view.adapter.ShowListRecyclerViewAdapter
import com.ss.entertainment.viewmodel.ListViewModel
import com.ss.entertainment.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_video_list.*

class ShowListFragment : Fragment() {

    private var columnCount = 1
    private var listener: OnListFragmentInteractionListener? = null
    private var videoListAdapter: ShowListRecyclerViewAdapter? = null

    enum class Status { API_CALL, COMPLETE_EMPTY, COMPLETE_NONEMPTY, ERROR }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_video_list, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        when {
            columnCount <= 1 -> recyclerViewList.layoutManager = LinearLayoutManager(context)
            else -> recyclerViewList.layoutManager = GridLayoutManager(context, columnCount)
        }
        videoListAdapter = ShowListRecyclerViewAdapter(listener)
        recyclerViewList.adapter = videoListAdapter

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        updateView(Status.API_CALL)
        val viewModel = ViewModelProviders.of(
            this, ViewModelFactory(
                activity!!.application
            )
        ).get(ListViewModel::class.java)

        viewModel.getListObservable().observe(this,
            Observer<List<Season>> { season ->
                if (season != null) {
                    updateView(Status.COMPLETE_NONEMPTY)
                    videoListAdapter?.setData(season)
                } else {
                    updateView(Status.COMPLETE_EMPTY)
                }
            })
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context

        } else {
            throw RuntimeException("$context must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun updateView(status: Status) {
        when (status) {
            Status.API_CALL -> {
                recyclerViewList.visibility = View.GONE
                textUserMessage.text = getString(R.string.loading)
                layoutInfo.visibility = View.VISIBLE
            }
            Status.COMPLETE_EMPTY -> {
                recyclerViewList.visibility = View.GONE
                textUserMessage.text = getString(R.string.empty_result)
                layoutInfo.visibility = View.VISIBLE
            }
            Status.COMPLETE_NONEMPTY -> {
                layoutInfo.visibility = View.GONE
                recyclerViewList.visibility = View.VISIBLE
            }
            Status.ERROR -> {
                recyclerViewList.visibility = View.GONE
                textUserMessage.text = getString(R.string.error_api_call)
                layoutInfo.visibility = View.VISIBLE

            }
        }
    }


    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(item: Season?)
    }

    companion object {
        const val ARG_COLUMN_COUNT = "column-count"
        const val TAG = "ShowListFragment"
        @JvmStatic
        fun newInstance(columnCount: Int) =
            ShowListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}
