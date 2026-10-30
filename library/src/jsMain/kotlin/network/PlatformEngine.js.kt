package com.github.livingwithhippos.realdebrid.network

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.js.Js

internal actual fun platformEngineFactory(): HttpClientEngineFactory<*> = Js
