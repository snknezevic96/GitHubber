package com.futuradev.githubber.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.futuradev.githubber.data.model.retrofit.response.UserResponse

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: UserResponse)

    @Query("SELECT * FROM UserResponse WHERE id = :id")
    fun getUser(id: Int) : UserResponse?

    @Query("Select * FROM UserResponse LIMIT 1")
    fun getUserLive() : LiveData<UserResponse?>

    @Query("DELETE FROM UserResponse")
    fun deleteUser()
}