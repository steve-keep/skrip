package com.bitperfect.app.player

import android.content.ComponentName
import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.bitperfect.app.library.TrackInfo
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.guava.await

class PlayerRepository(
    private val context: Context,
    private val factory: MediaControllerFactory = DefaultMediaControllerFactory()
) {

    fun interface MediaControllerFactory {
        fun build(context: Context, token: SessionToken): ListenableFuture<MediaController>
    }

    private class DefaultMediaControllerFactory : MediaControllerFactory {
        override fun build(context: Context, token: SessionToken): ListenableFuture<MediaController> {
            return MediaController.Builder(context, token).buildAsync()
        }
    }

    private var controller: MediaController? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentMediaId = MutableStateFlow<String?>(null)
    val currentMediaId: StateFlow<String?> = _currentMediaId.asStateFlow()

    private val _positionMs = MutableStateFlow(0L)
    val positionMs: StateFlow<Long> = _positionMs.asStateFlow()

    private val listener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = controller?.isPlaying ?: false
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            _currentMediaId.value = controller?.currentMediaItem?.mediaId
        }

        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            _positionMs.value = controller?.currentPosition ?: 0L
        }
    }

    suspend fun connect() {
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        controller = factory.build(context, sessionToken).await().apply {
            addListener(listener)
            // Initialize state
            _isPlaying.value = isPlaying
            _currentMediaId.value = currentMediaItem?.mediaId
            _positionMs.value = currentPosition
        }
    }

    fun disconnect() {
        controller?.apply {
            removeListener(listener)
            release()
        }
        controller = null
    }

    fun playAlbum(tracks: List<TrackInfo>) {
        playTrack(tracks, 0)
    }

    fun playTrack(tracks: List<TrackInfo>, index: Int) {
        val mediaItems = tracks.map { track ->
            val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, track.id)
            MediaItem.Builder()
                .setUri(uri)
                .setMediaId(track.id.toString())
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(track.title)
                        .setTrackNumber(track.trackNumber)
                        .build()
                )
                .build()
        }

        controller?.apply {
            setMediaItems(mediaItems)
            seekToDefaultPosition(index)
            prepare()
            play()
        }
    }

    fun togglePlayPause() {
        controller?.let {
            if (it.isPlaying) {
                it.pause()
            } else {
                it.play()
            }
        }
    }

    fun seekTo(ms: Long) {
        controller?.seekTo(ms)
    }

    fun skipNext() {
        controller?.seekToNext()
    }

    fun skipPrev() {
        controller?.seekToPrevious()
    }
}
