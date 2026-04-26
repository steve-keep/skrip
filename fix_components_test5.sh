#!/bin/bash
git checkout app/src/test/kotlin/com/bitperfect/app/ui/LibrarySectionTest.kt
git checkout app/src/test/kotlin/com/bitperfect/app/ui/SettingsScreenTest.kt
git checkout app/src/test/kotlin/com/bitperfect/app/ui/TrackListScreenTest.kt

for file in app/src/test/kotlin/com/bitperfect/app/ui/LibrarySectionTest.kt app/src/test/kotlin/com/bitperfect/app/ui/SettingsScreenTest.kt app/src/test/kotlin/com/bitperfect/app/ui/TrackListScreenTest.kt; do
    sed -i 's/val mockViewModel = AppViewModel(application)/val mockController = org.mockito.Mockito.mock(androidx.media3.session.MediaController::class.java)\n        val fakeFactory = object : com.bitperfect.app.player.PlayerRepository.MediaControllerFactory {\n            override fun build(context: android.content.Context, token: androidx.media3.session.SessionToken): com.google.common.util.concurrent.ListenableFuture<androidx.media3.session.MediaController> {\n                return com.google.common.util.concurrent.Futures.immediateFuture(mockController)\n            }\n        }\n        val fakeRepository = com.bitperfect.app.player.PlayerRepository(application, fakeFactory)\n        val mockViewModel = AppViewModel(application, fakeRepository)/g' $file
done
