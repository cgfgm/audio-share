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

package io.github.mkckr0.audio_share_app.ui.base

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import io.github.mkckr0.audio_share_app.R
import io.github.mkckr0.audio_share_app.model.appSettingsDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun IntPreference(
    icon: ImageVector? = null,
    key: String,
    title: String,
    defaultValue: Int,
    valueRange: IntRange,
    valueFormatter: (value: Int) -> String = { it.toString() },
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val prefKey = remember { intPreferencesKey(key) }
    val value = remember {
        context.appSettingsDataStore.data.map {
            it[prefKey] ?: defaultValue
        }
    }.collectAsState(null).value

    var showDialog by remember { mutableStateOf(false) }

    ListItem(
        leadingContent = { PreferenceIcon(icon) },
        headlineContent = { Text(title) },
        supportingContent = { Text(if (value == null) "" else valueFormatter(value)) },
        modifier = Modifier.clickable {
            if (value != null) {
                showDialog = true
            }
        }
    )

    if (showDialog && value != null) {
        var textField by remember(value) { mutableStateOf(value.toString()) }
        AlertDialog(
            onDismissRequest = { showDialog = false },
            icon = { PreferenceIcon(icon) },
            title = { Text(title) },
            text = {
                TextField(
                    value = textField,
                    onValueChange = { textField = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text("Cancel")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val newValue = textField.trim().toIntOrNull()
                        if (newValue == null || newValue !in valueRange) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.label_value_is_invalid),
                                Toast.LENGTH_SHORT
                            ).show()
                            return@TextButton
                        }
                        showDialog = false
                        scope.launch {
                            context.appSettingsDataStore.edit {
                                it[prefKey] = newValue
                            }
                        }
                    }
                ) {
                    Text("Apply")
                }
            },
        )
    }
}
