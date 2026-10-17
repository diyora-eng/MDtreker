package com.xemoado.mdtracker.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID

private val Context.sessionDataStore by preferencesDataStore(name = "session")

class SessionStore(private val context: Context, private val userDao: UserDao) {
    private val keyUserId = stringPreferencesKey("user_id")

    /** Текущий userId; эмитит значение только после ensureUser(). */
    val userId: Flow<String> = context.sessionDataStore.data
        .map { prefs -> prefs[keyUserId] }
        .filterNotNull()
        .distinctUntilChanged()

    /** Создаёт локальный профиль при первом запуске (UUID). Безопасно вызывать повторно. */
    suspend fun ensureUser() {
        val existingId = context.sessionDataStore.data.first()[keyUserId]
        if (existingId != null) {
            if (userDao.exists(existingId) == 0) userDao.insert(UserEntity(existingId))
            return
        }
        val id = UUID.randomUUID().toString()
        userDao.insert(UserEntity(id))
        context.sessionDataStore.edit { prefs -> prefs[keyUserId] = id }
    }

    /** Переключение аккаунта (когда появится авторизация). */
    suspend fun switchUser(newId: String) {
        userDao.insert(UserEntity(newId))
        context.sessionDataStore.edit { prefs -> prefs[keyUserId] = newId }
    }
}
