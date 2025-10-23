package com.nexarion.datossinmvvm
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow
@Dao
interface UserDao {

    @Query("SELECT * FROM User ORDER BY uid DESC")
    fun getAll(): Flow<List<User>> // <-- Ya no es 'suspend fun'

    @Insert
    suspend fun insert(user: User)


    @Delete
    suspend fun delete(user: User)

    // (Dejamos esta por el ejercicio anterior)
    @Query("DELETE FROM User WHERE uid = (SELECT MAX(uid) FROM User)")
    suspend fun deleteLastUser()
}