#!/bin/bash
git checkout app/src/test/kotlin/com/bitperfect/app/MainActivityRobolectricTest.kt app/src/test/kotlin/com/bitperfect/app/ui/LibrarySectionTest.kt app/src/test/kotlin/com/bitperfect/app/ui/SettingsScreenTest.kt app/src/test/kotlin/com/bitperfect/app/ui/TrackListScreenTest.kt app/src/test/kotlin/com/bitperfect/app/ui/ComponentsTest.kt

# Inject Application to PlayerRepository but without overriding tests with mockito since tests don't connect
