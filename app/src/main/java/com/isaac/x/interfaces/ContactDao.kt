package com.isaac.x.interfaces

import androidx.lifecycle.LiveData
import androidx.room.*
import com.isaac.x.models.ContactEntity

@Dao
interface ContactDao {

    @Query("SELECT * FROM contacts")
    fun getAllContacts() : LiveData<List<ContactEntity>>

    @Query("SELECT * FROM contacts WHERE contact LIKE :contact")
    fun getContact(contact: String) : LiveData<ContactEntity>

    @Insert
    suspend fun insertContacts(contactEntity: ContactEntity)

    @Update
    suspend fun updateContact(contactEntity: ContactEntity)

    @Query("DELETE FROM contacts WHERE contact = :contact")
    fun deleteContact(contact: String)
}