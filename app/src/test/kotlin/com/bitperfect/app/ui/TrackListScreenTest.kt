package com.bitperfect.app.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import com.bitperfect.app.library.ArtistInfo
import com.bitperfect.app.library.AlbumInfo
import com.bitperfect.app.library.TrackInfo

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class TrackListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun verifyTrackListScreenLoadingState() {
        val application = org.robolectric.RuntimeEnvironment.getApplication()
        val mockViewModel = AppViewModel(application, com.bitperfect.app.player.PlayerRepository(application, object : com.bitperfect.app.player.PlayerRepository.MediaControllerFactory { override fun build(context: android.content.Context, token: androidx.media3.session.SessionToken) = com.google.common.util.concurrent.Futures.immediateFuture(org.mockito.Mockito.mock(androidx.media3.session.MediaController::class.java)) }))
        mockViewModel.selectAlbum(1L, "Test Album")

        composeTestRule.setContent {
            TrackListScreen(viewModel = mockViewModel)
        }

        composeTestRule.onNodeWithText("Loading tracks...").assertIsDisplayed()
    }

    @Test
    fun verifyTrackListScreenLoadedState() {
        val application = org.robolectric.RuntimeEnvironment.getApplication()
        val mockViewModel = AppViewModel(application, com.bitperfect.app.player.PlayerRepository(application, object : com.bitperfect.app.player.PlayerRepository.MediaControllerFactory { override fun build(context: android.content.Context, token: androidx.media3.session.SessionToken) = com.google.common.util.concurrent.Futures.immediateFuture(org.mockito.Mockito.mock(androidx.media3.session.MediaController::class.java)) }))

        val albums = listOf(
            AlbumInfo(id = 1L, title = "Test Album", artUri = null)
        )
        val artists = listOf(
            ArtistInfo(id = 1L, name = "Test Artist", albums = albums)
        )

        val artistsFlowField = AppViewModel::class.java.getDeclaredField("_artists")
        artistsFlowField.isAccessible = true
        val flow = artistsFlowField.get(mockViewModel) as MutableStateFlow<List<ArtistInfo>>
        flow.value = artists

        mockViewModel.selectAlbum(1L, "Test Album")

        val tracks = listOf(
            TrackInfo(id = 1L, title = "Test Track", trackNumber = 1, durationMs = 60000L)
        )

        val tracksFlowField = AppViewModel::class.java.getDeclaredField("_tracks")
        tracksFlowField.isAccessible = true
        val tracksFlow = tracksFlowField.get(mockViewModel) as MutableStateFlow<List<TrackInfo>>
        tracksFlow.value = tracks

        composeTestRule.setContent {
            TrackListScreen(viewModel = mockViewModel)
        }

        composeTestRule.onNodeWithText("Test Track").assertIsDisplayed()
        composeTestRule.onNodeWithText("1:00").assertIsDisplayed()
    }
}
