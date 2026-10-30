package com.github.livingwithhippos.realdebrid.network

import io.ktor.client.engine.HttpClientEngineFactory

internal expect fun platformEngineFactory(): HttpClientEngineFactory<*>
