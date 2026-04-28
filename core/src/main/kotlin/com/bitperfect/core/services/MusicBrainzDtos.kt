package com.bitperfect.core.services

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable data class MbDiscIdResponse(val releases: List<MbRelease>)

@Serializable data class MbRelease(
    val id: String,
    val title: String,
    @SerialName("artist-credit") val artistCredit: List<MbArtistCredit>,
    val media: List<MbMedia>
)

@Serializable data class MbArtistCredit(val artist: MbArtist)

@Serializable data class MbArtist(val name: String)

@Serializable data class MbMedia(val tracks: List<MbTrack>)

@Serializable data class MbTrack(val title: String)
