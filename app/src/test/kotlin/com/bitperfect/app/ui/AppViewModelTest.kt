package com.bitperfect.app.ui

import android.app.Application
import android.content.Context
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.test.core.app.ApplicationProvider
import com.bitperfect.app.library.ArtistInfo
import com.bitperfect.app.library.TrackInfo
import com.bitperfect.app.player.PlayerRepository
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class AppViewModelTest {

    private lateinit var viewModel: AppViewModel

    @Before
    fun setup() {
        val application = ApplicationProvider.getApplicationContext<Application>()
        val fakeFactory = object : PlayerRepository.MediaControllerFactory {
            override fun build(context: Context, token: SessionToken): ListenableFuture<MediaController> {
                return Futures.immediateFuture(org.mockito.Mockito.mock(MediaController::class.java))
            }
        }
        val fakeRepository = PlayerRepository(application, fakeFactory)
        viewModel = AppViewModel(application, fakeRepository)
    }

    @Test
    fun testClearTracks() {
        assertEquals(emptyList<TrackInfo>(), viewModel.tracks.value)
        viewModel.clearTracks()
        assertEquals(emptyList<TrackInfo>(), viewModel.tracks.value)
    }

    @Test
    fun testSearchQueryFilter() {
        viewModel.searchQuery.value = "test"
        assertEquals("test", viewModel.searchQuery.value)
    }

    @Test
    fun testSelectAlbumAndLoadTracks() {
        viewModel.selectAlbum(123L, "Test Album")
        assertEquals(123L, viewModel.selectedAlbumId.value)
        assertEquals("Test Album", viewModel.selectedAlbumTitle.value)
    }
}
