package com.isaac.x

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.isaac.x.interfaces.ContactDao
import com.isaac.x.models.ContactEntity

@Database(entities = [ContactEntity::class], version = 1)
abstract class ContactDatabase : RoomDatabase() {

    abstract fun contactDao() : ContactDao

    companion object{
        @Volatile private var instance: ContactDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also { instance = it}
        }

        private fun buildDatabase(context: Context) = Room
            .databaseBuilder(context, ContactDatabase::class.java, "contacts.db")
            .build()
    }

}