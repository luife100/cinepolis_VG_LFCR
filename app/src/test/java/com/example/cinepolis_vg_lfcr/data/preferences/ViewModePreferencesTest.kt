package com.example.cinepolis_vg_lfcr.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class ViewModePreferencesTest {

    @JvmField
    @Rule
    val tmpFolder = TemporaryFolder()

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var preferences: ViewModePreferences

    @Before
    fun setUp() {
        dataStore = mockk()
        every { dataStore.data } returns flowOf(preferencesOf())
        preferences = ViewModePreferences(dataStore)
    }

    @Test
    fun viewModeValue_emitsStoredValue() = runTest {
        val key = stringPreferencesKey("view_mode")
        val storeWithGrid = mockk<DataStore<Preferences>>()
        every { storeWithGrid.data } returns flowOf(preferencesOf(key to "Grid"))
        val prefs = ViewModePreferences(storeWithGrid)
        val list = mutableListOf<String>()
        prefs.viewModeValue.collect { list.add(it) }
        assertEquals(listOf("Grid"), list)
    }

    @Test
    fun viewModeValue_emitsListWhenKeyMissing() = runTest {
        every { dataStore.data } returns flowOf(preferencesOf())
        val list = mutableListOf<String>()
        preferences.viewModeValue.collect { list.add(it) }
        assertEquals(listOf("List"), list)
    }

    @Test
    fun setViewModeValue_persistsAndEmitsValue() = runTest {
        val file = tmpFolder.newFile("test_prefs.preferences_pb")
        val realDataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create(
            produceFile = { file }
        )
        val realPrefs = ViewModePreferences(realDataStore)

        realPrefs.setViewModeValue("Grid")
        val list = mutableListOf<String>()
        realPrefs.viewModeValue.take(1).collect { list.add(it) }
        assertEquals("Grid", list.single())

        realPrefs.setViewModeValue("List")
        val list2 = mutableListOf<String>()
        realPrefs.viewModeValue.take(1).collect { list2.add(it) }
        assertEquals("List", list2.single())
    }
}
