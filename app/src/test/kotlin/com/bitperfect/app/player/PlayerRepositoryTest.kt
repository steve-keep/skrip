package com.bitperfect.app.player

import android.content.Context
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class PlayerRepositoryTest {

    @Test
    fun `test initial state`() = runTest {
        val mockContext = mock(Context::class.java)
        `when`(mockContext.packageName).thenReturn("com.bitperfect.app")
        val mockController = mock(MediaController::class.java)

        val fakeFactory = object : PlayerRepository.MediaControllerFactory {
            override fun build(context: Context, token: SessionToken): ListenableFuture<MediaController> {
                return Futures.immediateFuture(mockController)
            }
        }

        val repository = PlayerRepository(mockContext, fakeFactory)

        assertEquals(false, repository.isPlaying.value)
        assertNull(repository.currentMediaId.value)
        assertEquals(0L, repository.positionMs.value)
    }
}
