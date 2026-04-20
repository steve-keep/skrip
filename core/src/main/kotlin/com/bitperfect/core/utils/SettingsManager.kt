package com.bitperfect.core.utils

import android.content.Context
import android.content.SharedPreferences
import com.bitperfect.core.engine.DriveCapabilities
import com.bitperfect.core.engine.TestCd

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("bitperfect_settings", Context.MODE_PRIVATE)

    var isVirtualDriveEnabled: Boolean
        get() = prefs.getBoolean("isVirtualDriveEnabled", false)
        set(value) = prefs.edit().putBoolean("isVirtualDriveEnabled", value).apply()

    var selectedTestCdIndex: Int
        get() = prefs.getInt("selectedTestCdIndex", 0)
        set(value) = prefs.edit().putInt("selectedTestCdIndex", value).apply()

    var outputFolderUri: String?
        get() = prefs.getString("outputFolderUri", null)
        set(value) = prefs.edit().putString("outputFolderUri", value).apply()

    fun saveDriveCapabilities(id: String, caps: DriveCapabilities) {
        prefs.edit()
            .putString("caps_${id}_vendor", caps.vendor)
            .putString("caps_${id}_product", caps.product)
            .putString("caps_${id}_revision", caps.revision)
            .putBoolean("caps_${id}_accurateStream", caps.accurateStream)
            .putInt("caps_${id}_readOffset", caps.readOffset)
            .putBoolean("caps_${id}_hasCache", caps.hasCache)
            .putInt("caps_${id}_cacheSizeKb", caps.cacheSizeKb)
            .putBoolean("caps_${id}_supportsC2", caps.supportsC2)
            .putBoolean("caps_${id}_offsetFromAccurateRip", caps.offsetFromAccurateRip)
            .apply()
    }

    fun getDriveCapabilities(id: String): DriveCapabilities? {
        if (!prefs.contains("caps_${id}_vendor")) return null
        return DriveCapabilities(
            vendor = prefs.getString("caps_${id}_vendor", "") ?: "",
            product = prefs.getString("caps_${id}_product", "") ?: "",
            revision = prefs.getString("caps_${id}_revision", "") ?: "",
            accurateStream = prefs.getBoolean("caps_${id}_accurateStream", false),
            readOffset = prefs.getInt("caps_${id}_readOffset", 0),
            hasCache = prefs.getBoolean("caps_${id}_hasCache", false),
            cacheSizeKb = prefs.getInt("caps_${id}_cacheSizeKb", 0),
            supportsC2 = prefs.getBoolean("caps_${id}_supportsC2", false),
            offsetFromAccurateRip = prefs.getBoolean("caps_${id}_offsetFromAccurateRip", false)
        )
    }

    companion object {
        val NEVERMIND_MOCK = TestCd(
            artist = "Nirvana",
            album = "Nevermind (real AR data)",
            tracks = listOf(
                "Smells Like Teen Spirit", "In Bloom", "Come as You Are",
                "Breed", "Lithium", "Polly", "Territorial Pissings",
                "Drain You", "Lounge Act", "Stay Away", "On a Plain",
                "Something in the Way"
            ),
            customTrackOffsets = IntArray(100).apply {
                val offsets = intArrayOf(
                    150, 22767, 41887, 58317, 72102,
                    91375, 104652, 115380, 132165,
                    143932, 159870, 174597
                )
                for (i in offsets.indices) {
                    this[i + 1] = offsets[i]
                }
                this[0] = 267225 // Lead-out
            },
            accurateRipId1 = 0x00151845u,
            accurateRipId2 = 0x00C504B0u,
            cddbId = 0xA70DE90C.toInt(),
            trackCrcsV1 = intArrayOf(
                0x279cf130, 0x1129357c, 0x69471ca4, 0xe47641d0.toInt(), 0x298bfe13,
                0x97248fcf.toInt(), 0x4c4d45b, 0xfdbc889e.toInt(), 0x2109a82d, 0x5439ff6e,
                0xe4d98eb6.toInt(), 0x2d99e3b5
            ),
            trackCrcsV2 = intArrayOf(
                0x2257a3d0, 0xcf27f368.toInt(), 0xeee75fdb.toInt(), 0x5576a40f, 0xfe24b956.toInt(),
                0xb656d13, 0xea7cd2d5.toInt(), 0x1c4a4d4a, 0xcd3b615b.toInt(), 0xa0e20c44.toInt(),
                0x22702c9c, 0xcb1c5775.toInt()
            ),
            confidence = intArrayOf(18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 19)
        )
    }

    val testCds = listOf(
        NEVERMIND_MOCK
    )

    fun getSelectedTestCd(): TestCd {
        return testCds.getOrElse(selectedTestCdIndex) { testCds[0] }
    }
}
