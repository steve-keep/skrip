#!/bin/bash
git checkout app/src/test/kotlin/com/bitperfect/app/ui/LibrarySectionTest.kt app/src/test/kotlin/com/bitperfect/app/ui/SettingsScreenTest.kt app/src/test/kotlin/com/bitperfect/app/ui/TrackListScreenTest.kt app/src/test/kotlin/com/bitperfect/app/ui/ComponentsTest.kt app/src/test/kotlin/com/bitperfect/app/MainActivityRobolectricTest.kt

for file in app/src/test/kotlin/com/bitperfect/app/ui/LibrarySectionTest.kt app/src/test/kotlin/com/bitperfect/app/ui/SettingsScreenTest.kt app/src/test/kotlin/com/bitperfect/app/ui/TrackListScreenTest.kt; do
    sed -i 's/val mockViewModel = AppViewModel(application)/val fakeFactory = object : com.bitperfect.app.player.PlayerRepository.MediaControllerFactory { override fun build(context: android.content.Context, token: androidx.media3.session.SessionToken) = com.google.common.util.concurrent.Futures.immediateFuture(org.mockito.Mockito.mock(androidx.media3.session.MediaController::class.java)) }\n        val mockViewModel = AppViewModel(application, com.bitperfect.app.player.PlayerRepository(application, fakeFactory))/g' $file
done

sed -i 's/fun verifyConnectingState()/fun verifyConnectingState() = kotlinx.coroutines.test.runTest/g' app/src/test/kotlin/com/bitperfect/app/ui/ComponentsTest.kt
sed -i 's/fun verifyEmptyState()/fun verifyEmptyState() = kotlinx.coroutines.test.runTest/g' app/src/test/kotlin/com/bitperfect/app/ui/ComponentsTest.kt
sed -i 's/fun verifyDiscReadyState()/fun verifyDiscReadyState() = kotlinx.coroutines.test.runTest/g' app/src/test/kotlin/com/bitperfect/app/ui/ComponentsTest.kt
