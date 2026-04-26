#!/bin/bash
sed -i 's/fun verifyEmptyStateDisplaysMusicNoteAndText() {/fun verifyEmptyStateDisplaysMusicNoteAndText() = kotlinx.coroutines.test.runTest {/g' app/src/test/kotlin/com/bitperfect/app/ui/LibrarySectionTest.kt
