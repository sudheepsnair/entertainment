package com.ss.entertainment.view.widget



import android.content.Context
import android.graphics.Point
import android.net.Uri
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet

import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AbsListView
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import com.google.android.exoplayer2.*

import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.ss.entertainment.PlaybackParameters.MAX_BUFFER_DURATION
import com.ss.entertainment.PlaybackParameters.MIN_BUFFER_DURATION
import com.ss.entertainment.PlaybackParameters.MIN_PLAYBACK_RESUME_BUFFER
import com.ss.entertainment.PlaybackParameters.MIN_PLAYBACK_START_BUFFER
import com.ss.entertainment.model.Episode
import com.ss.entertainment.view.adapter.EpisodesRecyclerViewAdapter

class VideoPlaybackRecyclerView : RecyclerView {

    private var mEpisodes: List<Episode>? = null
    private var mVideoSurfaceDefaultHeight = 0
    private var mScreenDefaultHeight = 0
    internal var mPlayer: SimpleExoPlayer? = null
    private var mVideoSurfaceView: PlayerView? = null
    private var mThumbnailImage: ImageView? = null
    private var mProgressBar: ProgressBar? = null
    private var mAppContext: Context? = null


    private var mPlayPosition = -1
    private var mAddedVideo = false
    private var mRowParent: View? = null


    constructor(context: Context) : super(context) {
        initialize(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet
    ) : super(context, attrs) {
        initialize(context)
    }


    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        initialize(context)
    }

    fun setVideoInfoList(episodes: List<Episode>?) {
        mEpisodes = episodes

    }

    /**
     * prepare for video play
     */
    //remove the mPlayer from the row
    private fun removeVideoView(videoView: PlayerView) {

        val parent = videoView.parent  as ViewGroup

        val index = parent.indexOfChild(videoView)
        if (index >= 0) {
            parent.removeViewAt(index)
            mAddedVideo = false
        }

    }

    //play the video in the row
    fun playVideo() {
        val startPosition = (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        var endPosition = (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()

        if (endPosition - startPosition > 1)  endPosition = startPosition + 1

        if (startPosition < 0 || endPosition < 0) return


        val targetPosition: Int  = if (startPosition != endPosition) {
            val startPositionVideoHeight = getVisibleVideoSurfaceHeight(startPosition)
            val endPositionVideoHeight = getVisibleVideoSurfaceHeight(endPosition)
            if (startPositionVideoHeight > endPositionVideoHeight) startPosition else endPosition
        } else {
            startPosition
        }

        if (targetPosition < 0 || targetPosition == mPlayPosition) {
            return
        }
        mPlayPosition = targetPosition
        if (mVideoSurfaceView == null) {
            return
        }
        mVideoSurfaceView!!.visibility = View.INVISIBLE
        //removeVideoView(mVideoSurfaceView!!)  //TODO :

        // get target View targetPosition in RecyclerView
        val at = targetPosition - (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

        val child = getChildAt(at) ?: return

        val holder = child.tag as EpisodesRecyclerViewAdapter.ViewHolder

        mThumbnailImage = holder.mThumbnail
        mProgressBar = holder.mProgressBar
        val frameLayout = holder.mVideoLayout as FrameLayout
        frameLayout.addView(mVideoSurfaceView)
        mAddedVideo = true
        mRowParent = holder.itemView
        mVideoSurfaceView!!.requestFocus()
        // Bind the mPlayer to the view.
        mVideoSurfaceView!!.setPlayer(mPlayer)

        // Measures bandwidth during playback. Can be null if not required.
        val defaultBandwidthMeter = DefaultBandwidthMeter()
        // Produces DataSource instances through which media data is loaded.

        val dataSourceFactory = DefaultDataSourceFactory(
            mAppContext!!,
            Util.getUserAgent(mAppContext, "Entertainment"), defaultBandwidthMeter)
        // This is the MediaSource representing the media to be played.
        val uriString = mEpisodes?.get(targetPosition)?.videoUrl
        if (uriString != null) {
            val videoSource = ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(uriString))
            // Prepare the mPlayer with the source.
            mPlayer!!.prepare(videoSource)
            mPlayer!!.playWhenReady = true
        }


    }

    private fun getVisibleVideoSurfaceHeight(playPosition: Int): Int {
        val at = playPosition - (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

        val child = getChildAt(at) ?: return 0

        val location01 = IntArray(2)
        child.getLocationInWindow(location01)

        return if (location01[1] < 0) {
            location01[1] + mVideoSurfaceDefaultHeight
        } else {
            mScreenDefaultHeight - location01[1]
        }
    }


    private fun initialize(context: Context) {

        mAppContext = context.applicationContext
        val display = (getContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val point = Point()
        display.getSize(point)
        mVideoSurfaceDefaultHeight = point.x

        mScreenDefaultHeight = point.y
        mVideoSurfaceView = PlayerView(mAppContext)
        mVideoSurfaceView!!.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM

        val bandwidthMeter = DefaultBandwidthMeter()
        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        val loadControl  : DefaultLoadControl =DefaultLoadControl.Builder()
            .setBufferDurationsMs(MIN_BUFFER_DURATION, MAX_BUFFER_DURATION, MIN_PLAYBACK_START_BUFFER, MIN_PLAYBACK_START_BUFFER)
            .createDefaultLoadControl()

        // 2. Create the mPlayer
        mPlayer = ExoPlayerFactory.newSimpleInstance(mAppContext, trackSelector, loadControl)

        // Bind the mPlayer to the view.
        mVideoSurfaceView!!.useController = false
        mVideoSurfaceView!!.setPlayer(mPlayer)

        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    playVideo()
                }
            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }
        })

        addOnChildAttachStateChangeListener(object : RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {

            }

            override fun onChildViewDetachedFromWindow(view: View) {
                if (mAddedVideo && mRowParent != null && mRowParent == view) {
                    removeVideoView(mVideoSurfaceView!!)
                    mPlayPosition = -1
                    mVideoSurfaceView!!.visibility = View.INVISIBLE
                }

            }
        })
        mPlayer!!.addListener(object : Player.EventListener {
            override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {

            }

            override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {

            }

            override fun onLoadingChanged(isLoading: Boolean) {

            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                when (playbackState) {

                    Player.STATE_BUFFERING -> {
                        mVideoSurfaceView!!.alpha = 0.5f
                       // Log.e(TAG, "onPlayerStateChanged: Buffering ")
                        if (mProgressBar != null) {
                            mProgressBar!!.visibility = View.VISIBLE
                        }
                    }
                    Player.STATE_ENDED -> mPlayer!!.seekTo(0)
                    Player.STATE_IDLE -> {
                    }
                    Player.STATE_READY -> {
                        //Log.e(TAG, "onPlayerStateChanged: Ready ")
                        if (mProgressBar != null) {
                            mProgressBar!!.visibility = View.GONE
                        }
                        mVideoSurfaceView!!.visibility = View.VISIBLE
                        mVideoSurfaceView!!.alpha = 1f
                        mThumbnailImage!!.visibility = View.GONE
                    }
                    else -> {
                    }
                }
            }

            override fun onRepeatModeChanged(repeatMode: Int) {

            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {

            }

            override fun onPlayerError(error: ExoPlaybackException?) {

            }

            override fun onPositionDiscontinuity(reason: Int) {

            }

            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {

            }

            override fun onSeekProcessed() {

            }
        })
    }

    fun onPausePlayer() {
        if (mVideoSurfaceView != null) {
            removeVideoView(mVideoSurfaceView!!)
            mPlayer!!.release()
            mVideoSurfaceView = null
        }
    }

    fun onRestartPlayer() {
        if (mVideoSurfaceView == null) {
            mPlayPosition = -1
            playVideo()
        }
    }


    fun onRelease() {

        if (mPlayer != null) {
            mPlayer!!.release()
            mPlayer = null
        }

        mRowParent = null
    }

    companion object {
        private val TAG = "VideoPlaybackRecyclerView"
    }


}

