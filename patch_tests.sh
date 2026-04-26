#!/bin/bash

# Fix ComponentsTest: use runTest for UI test compatibility and add application setup
sed -i 's/fun verifyConnectingState()/fun verifyConnectingState() = kotlinx.coroutines.test.runTest/g' app/src/test/kotlin/com/bitperfect/app/ui/ComponentsTest.kt
sed -i 's/fun verifyEmptyState()/fun verifyEmptyState() = kotlinx.coroutines.test.runTest/g' app/src/test/kotlin/com/bitperfect/app/ui/ComponentsTest.kt
sed -i 's/fun verifyDiscReadyState()/fun verifyDiscReadyState() = kotlinx.coroutines.test.runTest/g' app/src/test/kotlin/com/bitperfect/app/ui/ComponentsTest.kt

# Inject fake PlayerRepository into ViewModels created in tests
for file in app/src/test/kotlin/com/bitperfect/app/ui/LibrarySectionTest.kt app/src/test/kotlin/com/bitperfect/app/ui/SettingsScreenTest.kt app/src/test/kotlin/com/bitperfect/app/ui/TrackListScreenTest.kt; do
    sed -i 's/val mockViewModel = AppViewModel(application)/val mockController = org.mockito.Mockito.mock(androidx.media3.session.MediaController::class.java)\n        val fakeFactory = object : com.bitperfect.app.player.PlayerRepository.MediaControllerFactory {\n            override fun build(context: android.content.Context, token: androidx.media3.session.SessionToken): com.google.common.util.concurrent.ListenableFuture<androidx.media3.session.MediaController> {\n                return com.google.common.util.concurrent.Futures.immediateFuture(mockController)\n            }\n        }\n        val fakeRepository = com.bitperfect.app.player.PlayerRepository(application, fakeFactory)\n        val mockViewModel = AppViewModel(application, fakeRepository)/g' $file
done
