#!/bin/bash
cat << 'XML' > app/src/test/AndroidManifest.xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <application>
        <activity android:name="androidx.activity.ComponentActivity" android:exported="true" />
    </application>
</manifest>
XML

sed -i 's/val mockViewModel = AppViewModel(application)/val fakeRepository = com.bitperfect.app.player.PlayerRepository(application, object : com.bitperfect.app.player.PlayerRepository.MediaControllerFactory { override fun build(context: android.content.Context, token: androidx.media3.session.SessionToken) = com.google.common.util.concurrent.Futures.immediateFuture(org.mockito.Mockito.mock(androidx.media3.session.MediaController::class.java)) })\n        val mockViewModel = AppViewModel(application, fakeRepository)/g' app/src/test/kotlin/com/bitperfect/app/ui/LibrarySectionTest.kt
sed -i 's/val mockViewModel = AppViewModel(application)/val fakeRepository = com.bitperfect.app.player.PlayerRepository(application, object : com.bitperfect.app.player.PlayerRepository.MediaControllerFactory { override fun build(context: android.content.Context, token: androidx.media3.session.SessionToken) = com.google.common.util.concurrent.Futures.immediateFuture(org.mockito.Mockito.mock(androidx.media3.session.MediaController::class.java)) })\n        val mockViewModel = AppViewModel(application, fakeRepository)/g' app/src/test/kotlin/com/bitperfect/app/ui/SettingsScreenTest.kt
sed -i 's/val mockViewModel = AppViewModel(application)/val fakeRepository = com.bitperfect.app.player.PlayerRepository(application, object : com.bitperfect.app.player.PlayerRepository.MediaControllerFactory { override fun build(context: android.content.Context, token: androidx.media3.session.SessionToken) = com.google.common.util.concurrent.Futures.immediateFuture(org.mockito.Mockito.mock(androidx.media3.session.MediaController::class.java)) })\n        val mockViewModel = AppViewModel(application, fakeRepository)/g' app/src/test/kotlin/com/bitperfect/app/ui/TrackListScreenTest.kt
