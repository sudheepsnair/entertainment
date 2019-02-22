package com.ss.entertainment

object PlaybackParameters {

    const val MIN_BUFFER_DURATION = 3000
    //Max Video you want to buffer during PlayBack
    const val MAX_BUFFER_DURATION = 5000
    //Min Video you want to buffer before start Playing it
    const val MIN_PLAYBACK_START_BUFFER = 1500
    //Min video You want to buffer when user resumes video
    const val MIN_PLAYBACK_RESUME_BUFFER = 5000
}