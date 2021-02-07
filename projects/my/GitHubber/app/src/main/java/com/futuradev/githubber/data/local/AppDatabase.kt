package com.futuradev.githubber.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.futuradev.githubber.data.model.retrofit.response.UserResponse

@Database(
    entities = [UserResponse::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase() : RoomDatabase() {

    abstract fun userDao() : UserDao
}