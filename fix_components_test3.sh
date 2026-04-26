#!/bin/bash
git checkout app/src/test/kotlin/com/bitperfect/app/ui/LibrarySectionTest.kt
git checkout app/src/test/kotlin/com/bitperfect/app/ui/SettingsScreenTest.kt
git checkout app/src/test/kotlin/com/bitperfect/app/ui/TrackListScreenTest.kt

for file in app/src/test/kotlin/com/bitperfect/app/MainActivityRobolectricTest.kt app/src/test/kotlin/com/bitperfect/app/ui/LibrarySectionTest.kt app/src/test/kotlin/com/bitperfect/app/ui/SettingsScreenTest.kt app/src/test/kotlin/com/bitperfect/app/ui/TrackListScreenTest.kt; do
    sed -i 's/AppViewModel(application)/AppViewModel(application, com.bitperfect.app.player.PlayerRepository(application, object : com.bitperfect.app.player.PlayerRepository.MediaControllerFactory { override fun build(context: android.content.Context, token: androidx.media3.session.SessionToken) = com.google.common.util.concurrent.Futures.immediateFuture(org.mockito.Mockito.mock(androidx.media3.session.MediaController::class.java)) }))/g' $file
done
