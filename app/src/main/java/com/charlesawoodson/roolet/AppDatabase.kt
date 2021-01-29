package com.charlesawoodson.roolet

import androidx.room.*

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}

@Entity
data class User(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "photoUri") val photoUri: String?,
    @ColumnInfo(name = "selectedNumber") val selectedNumber: String?
)

@Dao
interface UserDao {

    @Query("SELECT * FROM user")
    suspend fun getAll(): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<User>)

    @Delete
    suspend fun delete(user: User)

    @Delete
    suspend fun deleteAll(users: List<User>)

    @Update
    suspend fun updateAll(users: List<User>)

    @Query("UPDATE user SET selectedNumber = :selectedNumber WHERE id = :id")
    suspend fun updateSelectedNumberByContactId(id: Long, selectedNumber: String?)

}

interface DatabaseHelper {
    suspend fun getUsers(): List<User>
    suspend fun insertAll(users: List<User>)
    suspend fun deleteAll(users: List<User>)
    suspend fun updateAll(users: List<User>)
    suspend fun updateSelectedNumberByContactId(id: Long, selectedNumber: String?)
}

class DatabaseHelperImpl(private val appDatabase: AppDatabase) : DatabaseHelper {
    override suspend fun getUsers(): List<User> = appDatabase.userDao().getAll()

    override suspend fun insertAll(users: List<User>) = appDatabase.userDao().insertAll(users)

    override suspend fun deleteAll(users: List<User>) = appDatabase.userDao().deleteAll(users)

    override suspend fun updateAll(users: List<User>) = appDatabase.userDao().updateAll(users)

    override suspend fun updateSelectedNumberByContactId(id: Long, selectedNumber: String?) =
        appDatabase.userDao().updateSelectedNumberByContactId(id, selectedNumber)
}