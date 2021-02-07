package com.futuradev.githubber.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.futuradev.githubber.data.model.retrofit.response.UserResponse

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: UserResponse)

    @Query("SELECT * FROM UserResponse WHERE id = :id")
    fun getUser(id: Int) : UserResponse?
}