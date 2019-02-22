package com.ss.entertainment.view.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ss.entertainment.R
import com.ss.entertainment.model.Episode
import com.ss.entertainment.model.Season
import com.ss.entertainment.view.adapter.EpisodesRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_episodes.*

private const val ARG_PARAM1 = "param1"

class EpisodesFragment : Fragment() {

    private var mSeason: Season? = null
    private var listener: OnEpisodeFragmentInteractionListener? = null
    private var episodesRecyclerViewAdapter: EpisodesRecyclerViewAdapter? = null
    private var firstTime = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mSeason = it.get(ARG_PARAM1) as Season
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_episodes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        recyclerViewEpisodes.layoutManager = LinearLayoutManager(context)
        episodesRecyclerViewAdapter = EpisodesRecyclerViewAdapter(listener,mSeason)
        recyclerViewEpisodes.adapter = episodesRecyclerViewAdapter
        recyclerViewEpisodes.setVideoInfoList(mSeason?.episodes)

        if (firstTime) {
            Handler(Looper.getMainLooper()).post { recyclerViewEpisodes.playVideo() }
            firstTime = false
        }
        recyclerViewEpisodes.scrollToPosition(0)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnEpisodeFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnEpisodeFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        if (recyclerViewEpisodes != null)
            recyclerViewEpisodes.onRelease()
        listener = null
    }

    interface OnEpisodeFragmentInteractionListener {
        fun onEpisodeFragmentInteraction(episode: Episode)
    }

    companion object {
       @JvmStatic
        fun newInstance(season: Season) =
            EpisodesFragment().apply {
                arguments = Bundle().apply {
                   putParcelable(ARG_PARAM1,season)

                }
            }
    }
}
