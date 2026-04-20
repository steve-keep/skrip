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

    val testCds = listOf(
        TestCd(
            artist = "Nirvana",
            album = "Smells Like Teen Spirit (Promo)",
            tracks = listOf("Smells Like Teen Spirit"),
            customTrackOffsets = IntArray(100).apply {
                this[1] = 0
                this[0] = 22500 // ~5 minutes
                this[2] = 22500 // End of track 1 if lastTrack > 1
            }
        ),
        TestCd(
            artist = "Pink Floyd",
            album = "The Dark Side of the Moon",
            tracks = listOf(
                "Speak to Me / Breathe", "On the Run", "Time",
                "The Great Gig in the Sky", "Money", "Us and Them",
                "Any Colour You Like", "Brain Damage", "Eclipse"
            ),
            customTrackOffsets = IntArray(100).apply {
                val offsets = intArrayOf(150, 17846, 28809, 60523, 81239, 110942, 147137, 170102, 187329)
                for (i in offsets.indices) {
                    this[i + 1] = offsets[i]
                }
                this[0] = 199993 // Lead-out
            }
        ),
        TestCd(
            artist = "Fleetwood Mac",
            album = "Rumours",
            tracks = listOf(
                "Second Hand News", "Dreams", "Never Going Back Again",
                "Don't Stop", "Go Your Own Way", "Songbird", "The Chain",
                "You Make Loving Fun", "I Don't Want to Know", "Oh Daddy", "Gold Dust Woman"
            ),
            customTrackOffsets = IntArray(100).apply {
                val offsets = intArrayOf(150, 12837, 30410, 42505, 61395, 77770, 101235, 120077, 135095, 155105, 173925)
                for (i in offsets.indices) {
                    this[i + 1] = offsets[i]
                }
                this[0] = 193680 // Lead-out
            }
        ),
        TestCd(
            artist = "Nirvana",
            album = "Nevermind",
            tracks = listOf(
                "Smells Like Teen Spirit", "In Bloom", "Come as You Are",
                "Breed", "Lithium", "Polly", "Territorial Pissings",
                "Drain You", "Lounge Act", "Stay Away", "On a Plain",
                "Something in the Way"
            ),
            customTrackOffsets = IntArray(100).apply {
                val offsets = intArrayOf(150, 22627, 42288, 59146, 72322, 91583, 105345, 114759, 130913, 142438, 156510, 173085)
                for (i in offsets.indices) {
                    this[i + 1] = offsets[i]
                }
                this[0] = 189073 // Lead-out
            }
        ),
        TestCd(
            artist = "Daft Punk",
            album = "Random Access Memories",
            tracks = listOf(
                "Give Life Back to Music", "The Game of Love", "Giorgio by Moroder",
                "Within", "Instant Crush", "Lose Yourself to Dance", "Touch",
                "Get Lucky", "Beyond", "Motherboard", "Fragments of Time",
                "Doin' It Right", "Contact"
            ),
            customTrackOffsets = IntArray(100).apply {
                val offsets = intArrayOf(150, 20111, 44033, 84888, 100583, 125301, 150654, 187313, 214660, 235123, 260383, 280629, 303792)
                for (i in offsets.indices) {
                    this[i + 1] = offsets[i]
                }
                this[0] = 334335 // Lead-out
            }
        )
    )

    fun getSelectedTestCd(): TestCd {
        return testCds.getOrElse(selectedTestCdIndex) { testCds[0] }
    }
}
