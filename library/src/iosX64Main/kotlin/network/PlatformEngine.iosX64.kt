package com.github.livingwithhippos.realdebrid.network

import io.ktor.client.engine.darwin.Darwin

internal actual fun platformEngineFactory(): io.ktor.client.engine.HttpClientEngineFactory<*> = Darwin
