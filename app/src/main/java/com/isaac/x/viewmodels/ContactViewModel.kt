package com.isaac.x.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.isaac.x.interfaces.ContactDao
import com.isaac.x.models.ContactEntity
import kotlinx.coroutines.launch

class ContactViewModel(private val contactDao: ContactDao) : ViewModel() {

    val allContacts: LiveData<List<ContactEntity>> = contactDao.getAllContacts()

    fun getContact(contact: String): LiveData<ContactEntity>{
        return contactDao.getContact(contact)
    }

    fun addContact(contactEntity: ContactEntity){
        viewModelScope.launch {
            contactDao.insertContacts(contactEntity)
        }

    }

    class ContactViewModelFactory(private val contactDao: ContactDao) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ContactViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ContactViewModel(contactDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}