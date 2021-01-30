package com.charlesawoodson.roolet.db

import androidx.room.*
import com.charlesawoodson.roolet.contacts.model.GroupMember
import com.charlesawoodson.roolet.contacts.model.Phone
import com.google.gson.Gson

@Database(entities = [User::class, Group::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun groupDao(): GroupDao
}

class Converters {
    @TypeConverter
    fun phonesToJson(value: List<Phone>?): String = Gson().toJson(value)

    @TypeConverter
    fun jsonToPhones(value: String) = Gson().fromJson(value, Array<Phone>::class.java).toList()

    @TypeConverter
    fun groupMemberToJson(value: List<GroupMember>): String = Gson().toJson(value)

    @TypeConverter
    fun jsonToGroupMember(value: String) = Gson().fromJson(value, Array<GroupMember>::class.java).toList()
}

@Entity
data class User(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "photo_uri") val photoUri: String?,
    @ColumnInfo(name = "phones") val phones: List<Phone>?
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

}

@Entity
data class Group(
    @PrimaryKey(autoGenerate = true) val groupId: Long = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "members") val members: List<GroupMember>
)

@Dao
interface GroupDao {
    @Query("SELECT * FROM 'group'")
    suspend fun getAll(): List<Group>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: Group)

    @Delete
    suspend fun deleteGroup(group: Group)
}

interface DatabaseHelper {
    suspend fun getUsers(): List<User>
    suspend fun insertAll(users: List<User>)
    suspend fun deleteAll(users: List<User>)
    suspend fun updateAll(users: List<User>)

    suspend fun getGroups(): List<Group>
    suspend fun insertGroup(group: Group)
    suspend fun deleteGroup(group: Group)
}

class DatabaseHelperImpl(private val appDatabase: AppDatabase) : DatabaseHelper {
    // Contacts
    override suspend fun getUsers(): List<User> = appDatabase.userDao().getAll()

    override suspend fun insertAll(users: List<User>) = appDatabase.userDao().insertAll(users)

    override suspend fun deleteAll(users: List<User>) = appDatabase.userDao().deleteAll(users)

    override suspend fun updateAll(users: List<User>) = appDatabase.userDao().updateAll(users)

    // Groups
    override suspend fun getGroups(): List<Group> = appDatabase.groupDao().getAll()

    override suspend fun insertGroup(group: Group) = appDatabase.groupDao().insertGroup(group)

    override suspend fun deleteGroup(group: Group) = appDatabase.groupDao().deleteGroup(group)


}