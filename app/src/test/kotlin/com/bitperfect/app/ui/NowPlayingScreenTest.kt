package com.bitperfect.app.ui

import android.app.Application
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bitperfect.app.library.AlbumInfo
import com.bitperfect.app.library.ArtistInfo
import com.bitperfect.app.library.TrackInfo
import com.bitperfect.app.player.PlayerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class NowPlayingScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockRepository: PlayerRepository
    private lateinit var viewModel: AppViewModel

    @Before
    fun setup() {
        mockRepository = Mockito.mock(PlayerRepository::class.java)

        Mockito.`when`(mockRepository.isPlaying).thenReturn(MutableStateFlow(false))
        Mockito.`when`(mockRepository.currentMediaId).thenReturn(MutableStateFlow("1"))
        Mockito.`when`(mockRepository.currentTrackTitle).thenReturn(MutableStateFlow("Test Track Title"))
        Mockito.`when`(mockRepository.currentTrackArtist).thenReturn(MutableStateFlow("Test Artist"))
        Mockito.`when`(mockRepository.currentAlbumArtUri).thenReturn(MutableStateFlow(null))
        Mockito.`when`(mockRepository.positionMs).thenReturn(MutableStateFlow(1000L))

        val application = ApplicationProvider.getApplicationContext<Application>()
        viewModel = AppViewModel(application, mockRepository) { null }

        val tracks = listOf(
            TrackInfo(id = 1L, title = "Test Track Title", trackNumber = 1, durationMs = 120000L, albumId = 1L)
        )
        val albums = listOf(
            AlbumInfo(id = 1L, title = "Test Album", artUri = null)
        )
        val artists = listOf(
            ArtistInfo(id = 1L, name = "Test Artist", albums = albums)
        )

        val field = AppViewModel::class.java.getDeclaredField("_artists")
        field.isAccessible = true
        (field.get(viewModel) as MutableStateFlow<List<ArtistInfo>>).value = artists

        viewModel.playAlbum(tracks)
    }

    @Test
    fun testNowPlayingScreenRenders() {
        // Just checking basic render to increase coverage
        composeTestRule.setContent {
            NowPlayingScreen(viewModel = viewModel)
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Now Playing").assertExists()
        composeTestRule.onNodeWithText("Test Track Title").assertExists()
        composeTestRule.onNodeWithContentDescription("Play").assertExists()
        composeTestRule.onNodeWithContentDescription("Previous").assertExists()
        composeTestRule.onNodeWithContentDescription("Next").assertExists()

        composeTestRule.mainClock.advanceTimeBy(500)
    }

    @Test
    fun testNowPlayingScreenWithoutAlbum() {
        val application = ApplicationProvider.getApplicationContext<Application>()
        val vm2 = AppViewModel(application, mockRepository) { null }
        composeTestRule.setContent {
            NowPlayingScreen(viewModel = vm2)
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Now Playing").assertExists()
    }
}
