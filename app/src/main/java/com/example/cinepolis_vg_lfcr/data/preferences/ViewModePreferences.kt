package com.example.cinepolis_vg_lfcr.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val VIEW_MODE_KEY = stringPreferencesKey("view_mode")

class ViewModePreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    /** Stored value is "List" or "Grid". */
    val viewModeValue: Flow<String> = dataStore.data.map { prefs ->
        prefs[VIEW_MODE_KEY] ?: "List"
    }

    suspend fun setViewModeValue(value: String) {
        dataStore.edit { prefs ->
            prefs[VIEW_MODE_KEY] = value
        }
    }
}
