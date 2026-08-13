package com.example.milk_homecalci.data.database

import androidx.room.TypeConverter
import com.example.milk_homecalci.data.entity.Session

class Converters {
    @TypeConverter
    fun fromSession(session: Session): String {
        return session.name
    }

    @TypeConverter
    fun toSession(value: String): Session {
        return Session.valueOf(value)
    }
}
