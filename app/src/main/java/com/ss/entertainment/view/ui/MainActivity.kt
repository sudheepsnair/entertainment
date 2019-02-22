package com.ss.entertainment.view.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.ss.entertainment.R
import com.ss.entertainment.model.Episode
import com.ss.entertainment.model.Season

class MainActivity : AppCompatActivity(), ShowListFragment.OnListFragmentInteractionListener,
    EpisodesFragment.OnEpisodeFragmentInteractionListener {

    override fun onEpisodeFragmentInteraction(episode: Episode) {
         Toast.makeText(this, "Episode Selected", Toast.LENGTH_LONG).show()
    }

    override fun onListFragmentInteraction(season : Season?) {
        if (season != null) loadEpisodesFragment(season)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        seasonListFragment()
    }

    private fun seasonListFragment() {
        val videoListFragment = ShowListFragment.newInstance(1)
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_holder,
                videoListFragment,
                ShowListFragment.TAG
            )
            .commitNow()
    }

    private fun loadEpisodesFragment(season: Season) {
        val episodesFragment = EpisodesFragment.newInstance(season)
        supportFragmentManager
            .beginTransaction()
            .addToBackStack("season")
            .replace(
                R.id.fragment_holder,
                episodesFragment, null
            ).commit()
    }
}
