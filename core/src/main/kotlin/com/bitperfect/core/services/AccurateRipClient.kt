package com.bitperfect.core.services

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode

class AccurateRipClient(engine: HttpClientEngine? = null) {

    private var client = if (engine != null) {
        HttpClient(engine) {
            defaultRequest {
                header("User-Agent", "BitPerfect/1.0 (https://github.com/steve-keep/BitPerfect)")
            }
        }
    } else {
        HttpClient(OkHttp) {
            defaultRequest {
                header("User-Agent", "BitPerfect/1.0 (https://github.com/steve-keep/BitPerfect)")
            }
        }
    }

    suspend fun fetchBin(url: String): HttpResponse {
        return client.get(url)
    }
}
