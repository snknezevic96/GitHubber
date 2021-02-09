package com.futuradev.githubber.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.futuradev.githubber.data.model.entity.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User)

    @Query("SELECT * FROM User WHERE id = :id")
    fun getUser(id: Int) : User?

    @Query("Select * FROM User LIMIT 1")
    fun getUserLive() : LiveData<User?>

    @Query("DELETE FROM User")
    fun deleteUser()
}