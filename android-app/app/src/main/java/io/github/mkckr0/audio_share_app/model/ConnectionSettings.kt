/*
 *    Copyright 2022-2024 mkckr0 <https://github.com/mkckr0>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.mkckr0.audio_share_app.model

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import io.github.mkckr0.audio_share_app.R
import kotlinx.coroutines.flow.first

object ConnectionSettingsLimits {
    const val MIN_TIMEOUT_SECONDS = 1
    const val MAX_TIMEOUT_SECONDS = 300
    const val MIN_RETRY_INTERVAL_SECONDS = 1
    const val MAX_RETRY_INTERVAL_SECONDS = 3600
    const val MIN_RETRIES = 0
    const val MAX_RETRIES = 100
}

data class ConnectionSettings(
    val timeoutSeconds: Int,
    val retryIntervalSeconds: Int,
    val maxRetries: Int,
)

suspend fun Context.getConnectionSettings(): ConnectionSettings {
    val settings = appSettingsDataStore.data.first()
    return ConnectionSettings(
        timeoutSeconds = (
            settings[intPreferencesKey(AppSettingsKeys.CONNECTION_TIMEOUT_SECONDS)]
                ?: getInteger(R.integer.default_connection_timeout_seconds)
            ).coerceIn(
                ConnectionSettingsLimits.MIN_TIMEOUT_SECONDS,
                ConnectionSettingsLimits.MAX_TIMEOUT_SECONDS,
            ),
        retryIntervalSeconds = (
            settings[intPreferencesKey(AppSettingsKeys.RETRY_INTERVAL_SECONDS)]
                ?: getInteger(R.integer.default_retry_interval_seconds)
            ).coerceIn(
                ConnectionSettingsLimits.MIN_RETRY_INTERVAL_SECONDS,
                ConnectionSettingsLimits.MAX_RETRY_INTERVAL_SECONDS,
            ),
        maxRetries = (
            settings[intPreferencesKey(AppSettingsKeys.MAX_RETRIES)]
                ?: getInteger(R.integer.default_max_retries)
            ).coerceIn(
                ConnectionSettingsLimits.MIN_RETRIES,
                ConnectionSettingsLimits.MAX_RETRIES,
            ),
    )
}
