package com.bitperfect.core.models

import kotlinx.serialization.Serializable

@Serializable
data class DriveOffset(
    val drive: String,
    val vendor: String,
    val product: String,
    val offset: Int?,
    val submissions: Int?,
    val agreement: Int?
)

@Serializable
data class DriveOffsetsResponse(
    val generated_at: String,
    val drives: List<DriveOffset>
)
