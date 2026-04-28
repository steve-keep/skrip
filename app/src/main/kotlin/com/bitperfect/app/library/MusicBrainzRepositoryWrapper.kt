package com.bitperfect.app.library

import android.app.Application
import com.bitperfect.core.models.DiscMetadata
import com.bitperfect.core.models.DiscToc
import com.bitperfect.core.services.MusicBrainzRepository

open class MusicBrainzRepositoryWrapper(application: Application) {
    private val repository = MusicBrainzRepository(application)

    open suspend fun lookup(toc: DiscToc): DiscMetadata? {
        return repository.lookup(toc)
    }
}
