#!/bin/bash
git checkout app/src/test/kotlin/com/bitperfect/app/MainActivityRobolectricTest.kt app/src/test/kotlin/com/bitperfect/app/ui/LibrarySectionTest.kt app/src/test/kotlin/com/bitperfect/app/ui/SettingsScreenTest.kt app/src/test/kotlin/com/bitperfect/app/ui/TrackListScreenTest.kt app/src/test/kotlin/com/bitperfect/app/ui/ComponentsTest.kt

# Use standard runTest for all tests instead of kotlinx.coroutines.test.runTest where it causes issues
sed -i 's/fun verifyConnectingState() = kotlinx.coroutines.test.runTest/fun verifyConnectingState()/g' app/src/test/kotlin/com/bitperfect/app/ui/ComponentsTest.kt
sed -i 's/fun verifyEmptyState() = kotlinx.coroutines.test.runTest/fun verifyEmptyState()/g' app/src/test/kotlin/com/bitperfect/app/ui/ComponentsTest.kt
sed -i 's/fun verifyDiscReadyState() = kotlinx.coroutines.test.runTest/fun verifyDiscReadyState()/g' app/src/test/kotlin/com/bitperfect/app/ui/ComponentsTest.kt
