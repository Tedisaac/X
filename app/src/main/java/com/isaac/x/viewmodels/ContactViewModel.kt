package com.isaac.x.viewmodels

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.CallLog
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.isaac.x.interfaces.ContactDao
import com.isaac.x.models.ContactEntity
import kotlinx.coroutines.launch

class ContactViewModel(private val contactDao: ContactDao) : ViewModel() {

    private val PERMISSIONS_REQUEST_READ_CONTACTS = 100

    val allContacts: LiveData<List<ContactEntity>> = contactDao.getAllContacts()

    lateinit var contact: LiveData<ContactEntity>

    fun getContact(contact: String): LiveData<ContactEntity>{
        return contactDao.getContact(contact)
    }

    fun addContact(contactEntity: ContactEntity){
        viewModelScope.launch {
            contactDao.insertContacts(contactEntity)
        }

    }
    
    fun deleteContact(contact: String){
        viewModelScope.launch { 
            contactDao.deleteContact(contact)
        }
    }

    fun deleteCallLog(view: View, context: Context, numberTag: String){
        val number: CharArray = numberTag.toCharArray()
        var n = "%"
        for (i in number.indices) {
            n += (number[i].toString() + "%")
        }
        val queryString = CallLog.Calls.NUMBER + " LIKE '" + n + "'"
        try {
            context.contentResolver.delete(CallLog.Calls.CONTENT_URI, queryString, null)
            showSnackBar(view, "Logs Deleted Successfully!!")
            //finish()
        }catch (e: Exception){
            showSnackBar(view, e.message.toString())
            Log.e(ContentValues.TAG, "deleteCallLogByNumber: ${e.message}")
        }
    }

    fun showSnackBar(view: View, message: String){
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
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