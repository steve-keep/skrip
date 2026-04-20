package com.bitperfect.core.engine

import org.junit.Assert.assertEquals
import org.junit.Test
import com.bitperfect.core.utils.SettingsManager

class AccurateRipServiceTest {

    @Test
    fun accurateRipURLIsBuiltCorrectlyFromMockDisc() {
        val disc = SettingsManager.NEVERMIND_MOCK
        val service = AccurateRipService()

        val discId = AccurateRipDiscId(
            id1 = disc.accurateRipId1!!,
            id2 = disc.accurateRipId2!!,
            id3 = disc.cddbId!!.toUInt()
        )

        val urlName = service.generateAccurateRipUrlName(disc.tracks.size, discId)
        assertEquals("dBAR-012-00151845-00c504b0-a70de90c.bin", urlName)

        val id1Hex = "%08x".format(discId.id1.toLong())
        val x = id1Hex[id1Hex.length - 1]
        val y = id1Hex[id1Hex.length - 2]
        val z = id1Hex[id1Hex.length - 3]
        val urlPath = "accuraterip/${x}/${y}/${z}/${urlName}"

        assertEquals("accuraterip/5/4/8/dBAR-012-00151845-00c504b0-a70de90c.bin", urlPath)
    }

    @Test
    fun accurateRipCRCV1MatchesForAllTracks() {
        val disc = SettingsManager.NEVERMIND_MOCK
        val expectedCrcs = disc.trackCrcsV1!!

        assertEquals(12, expectedCrcs.size)
        assertEquals(0x279cf130, expectedCrcs[0])
    }
}
