package com.isaac.x.interfaces

import androidx.lifecycle.LiveData
import androidx.room.*
import com.isaac.x.models.ContactEntity

@Dao
interface ContactDao {

    @Query("SELECT * FROM contacts")
    fun getAllContacts() : List<ContactEntity>

    @Query("SELECT * FROM contacts WHERE contact LIKE :contact")
    fun getContact(contact: String) : LiveData<ContactEntity>

    @Insert
    fun insertContacts(contactEntity: ContactEntity)

    @Update
    fun updateContact(contactEntity: ContactEntity)

    @Delete
    fun deleteContact(contactEntity: ContactEntity)
}