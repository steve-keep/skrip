#!/bin/bash
git checkout app/src/test/kotlin/com/bitperfect/app/ui/ComponentsTest.kt

cat << 'FILE' > app/src/test/kotlin/com/bitperfect/app/ui/ComponentsTest.kt
package com.bitperfect.app.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import com.bitperfect.app.usb.DriveStatus
import com.bitperfect.app.usb.DriveInfo

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ComponentsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun verifyConnectingState() {
        composeTestRule.setContent {
            DeviceList(driveStatus = DriveStatus.Connecting(null))
        }

        composeTestRule.onNodeWithText("Connecting…").assertIsDisplayed()
    }

    @Test
    fun verifyEmptyState() {
        val driveInfo = DriveInfo("ASUS", "BW-16D1HT", true, 0, 0, "")
        composeTestRule.setContent {
            DeviceList(driveStatus = DriveStatus.Empty(driveInfo))
        }

        composeTestRule.onNodeWithText("No Disc Inserted").assertIsDisplayed()
    }

    @Test
    fun verifyDiscReadyState() {
        val driveInfo = DriveInfo("ASUS", "BW-16D1HT", true, 0, 0, "")
        composeTestRule.setContent {
            DeviceList(driveStatus = DriveStatus.DiscReady(driveInfo))
        }

        composeTestRule.onNodeWithText("Disc Ready").assertIsDisplayed()
    }
}
FILE

cat << 'FILE' > app/src/test/kotlin/com/bitperfect/app/ui/LibrarySectionTest.kt
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
import org.robolectric.shadows.ShadowLooper
import kotlinx.coroutines.test.runTest

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LibrarySectionTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun verifyEmptyStateDisplaysMusicNoteAndText() {
        val application = org.robolectric.RuntimeEnvironment.getApplication()
        val mockViewModel = AppViewModel(application, com.bitperfect.app.player.PlayerRepository(application, object : com.bitperfect.app.player.PlayerRepository.MediaControllerFactory { override fun build(context: android.content.Context, token: androidx.media3.session.SessionToken) = com.google.common.util.concurrent.Futures.immediateFuture(org.mockito.Mockito.mock(androidx.media3.session.MediaController::class.java)) }))

        val settingsManager = com.bitperfect.core.utils.SettingsManager(application)
        settingsManager.outputFolderUri = "content://dummy"
        mockViewModel.loadLibrary()

        composeTestRule.setContent {
            LibrarySection(viewModel = mockViewModel)
        }

        composeTestRule.onNodeWithText("No albums found").assertIsDisplayed()
    }

    @Test
    fun verifyNonEmptyStateDisplaysArtistsAndAlbums() = runTest {
        val application = org.robolectric.RuntimeEnvironment.getApplication()

        // Ensure folder configured is false so it doesn't run IO thread loads immediately
        val settingsManager = com.bitperfect.core.utils.SettingsManager(application)
        settingsManager.outputFolderUri = null

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

        // Setup output folder so it displays library
        val _isOutputFolderConfiguredField = AppViewModel::class.java.getDeclaredField("_isOutputFolderConfigured")
        _isOutputFolderConfiguredField.isAccessible = true
        val configuredFlow = _isOutputFolderConfiguredField.get(mockViewModel) as MutableStateFlow<Boolean>
        configuredFlow.value = true

        composeTestRule.setContent {
            LibrarySection(viewModel = mockViewModel)
        }

        composeTestRule.mainClock.advanceTimeBy(5000)
        composeTestRule.waitForIdle()
        composeTestRule.mainClock.advanceTimeBy(5000)

        composeTestRule.onNodeWithText("Test Artist").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Album").assertIsDisplayed()
    }
}
FILE

cat << 'FILE' > app/src/test/kotlin/com/bitperfect/app/ui/SettingsScreenTest.kt
package com.bitperfect.app.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import com.bitperfect.core.utils.SettingsManager
import com.bitperfect.core.services.DriveOffsetRepository

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun verifySettingsScreenRenders() {
        val application = org.robolectric.RuntimeEnvironment.getApplication()
        val mockViewModel = AppViewModel(application, com.bitperfect.app.player.PlayerRepository(application, object : com.bitperfect.app.player.PlayerRepository.MediaControllerFactory { override fun build(context: android.content.Context, token: androidx.media3.session.SessionToken) = com.google.common.util.concurrent.Futures.immediateFuture(org.mockito.Mockito.mock(androidx.media3.session.MediaController::class.java)) }))
        val settingsManager = SettingsManager(application)
        val driveOffsetRepository = DriveOffsetRepository(application)

        composeTestRule.setContent {
            SettingsScreen(
                driveOffsetRepository = driveOffsetRepository,
                settingsManager = settingsManager,
                viewModel = mockViewModel,
                onNavigateToAbout = {}
            )
        }

        composeTestRule.onNodeWithText("Output Folder").assertIsDisplayed()
    }
}
FILE

cat << 'FILE' > app/src/test/kotlin/com/bitperfect/app/ui/TrackListScreenTest.kt
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
FILE
