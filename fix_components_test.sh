#!/bin/bash
sed -i 's/fun verifyConnectingState() = kotlinx.coroutines.test.runTest {/fun verifyConnectingState() {/g' app/src/test/kotlin/com/bitperfect/app/ui/ComponentsTest.kt
sed -i 's/fun verifyEmptyState() = kotlinx.coroutines.test.runTest {/fun verifyEmptyState() {/g' app/src/test/kotlin/com/bitperfect/app/ui/ComponentsTest.kt
sed -i 's/fun verifyDiscReadyState() = kotlinx.coroutines.test.runTest {/fun verifyDiscReadyState() {/g' app/src/test/kotlin/com/bitperfect/app/ui/ComponentsTest.kt
