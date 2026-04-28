package com.bitperfect.core.models

data class TocEntry(val trackNumber: Int, val lba: Int)

data class DiscToc(
    val tracks: List<TocEntry>,   // audio tracks only, index 01 positions
    val leadOutLba: Int
) {
    val trackCount: Int get() = tracks.size
}
