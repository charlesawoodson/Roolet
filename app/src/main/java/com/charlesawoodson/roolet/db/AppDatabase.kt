package com.charlesawoodson.roolet.db

import android.os.Parcelable
import androidx.room.*
import com.charlesawoodson.roolet.contacts.model.GroupMember
import com.charlesawoodson.roolet.contacts.model.Phone
import com.google.gson.Gson
import io.reactivex.Observable
import kotlinx.android.parcel.Parcelize

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
    fun jsonToGroupMember(value: String) =
        Gson().fromJson(value, Array<GroupMember>::class.java).toList()
}

@Entity
data class User(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "photo_uri") val photoUri: String?,
    @ColumnInfo(name = "phones") val phones: List<Phone>?
)

@Parcelize
@Entity
data class Group(
    @PrimaryKey(autoGenerate = true) val groupId: Long = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "members") val members: List<GroupMember>
) : Parcelable

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

@Dao
interface GroupDao {
    @Query("SELECT * FROM 'group'")
    fun getGroups(): Observable<List<Group>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: Group)

    @Delete
    suspend fun deleteGroup(group: Group)

    @Query("SELECT * FROM 'group' WHERE groupId=:id")
    fun getGroupById(id: Long): Observable<Group>

    @Query("DELETE FROM 'group' WHERE groupId=:id")
    suspend fun deleteGroupById(id: Long)
}

interface DatabaseHelper {
    suspend fun getUsers(): List<User>
    suspend fun insertAll(users: List<User>)
    suspend fun deleteAll(users: List<User>)
    suspend fun updateAll(users: List<User>)

    fun getGroups(): Observable<List<Group>>
    suspend fun insertGroup(group: Group)
    suspend fun deleteGroup(group: Group)
    fun getGroupById(id: Long): Observable<Group>
    suspend fun deleteGroupById(id: Long)
}

class DatabaseHelperImpl(private val appDatabase: AppDatabase) : DatabaseHelper {
    // Contacts
    override suspend fun getUsers(): List<User> = appDatabase.userDao().getAll()

    override suspend fun insertAll(users: List<User>) = appDatabase.userDao().insertAll(users)

    override suspend fun deleteAll(users: List<User>) = appDatabase.userDao().deleteAll(users)

    override suspend fun updateAll(users: List<User>) = appDatabase.userDao().updateAll(users)

    // Groups
    override fun getGroups(): Observable<List<Group>> = appDatabase.groupDao().getGroups()

    override suspend fun insertGroup(group: Group) = appDatabase.groupDao().insertGroup(group)

    override suspend fun deleteGroup(group: Group) = appDatabase.groupDao().deleteGroup(group)

    override fun getGroupById(id: Long): Observable<Group> = appDatabase.groupDao().getGroupById(id)

    override suspend fun deleteGroupById(id: Long) = appDatabase.groupDao().deleteGroupById(id)
}