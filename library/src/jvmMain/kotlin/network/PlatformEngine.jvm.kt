package com.github.livingwithhippos.realdebrid.network

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.cio.CIO

internal actual fun platformEngineFactory(): HttpClientEngineFactory<*> = CIO
