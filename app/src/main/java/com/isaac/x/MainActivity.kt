package com.isaac.x

import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.CallLog
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar
import com.isaac.x.databinding.ActivityMainBinding
import com.isaac.x.models.ContactEntity
import com.isaac.x.viewmodels.ContactViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_READ_CONTACTS = 100

    private val numbers = ArrayList<String>()

    private val  contactsDb by lazy { ContactDatabase.getDatabase(this).contactDao() }

    private val mainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val viewModel: ContactViewModel by viewModels {
        ContactViewModel.ContactViewModelFactory(
            (application as ContactApplication).database.contactDao()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mainBinding.root)

        supportActionBar?.hide()

        setClickListeners()
        
        fetchContact()

    }

    private fun fetchContact() {
       viewModel.allContacts.observe(this){result ->
           Log.e(TAG, "fetchContact: $result", )
       }
    }

    private fun setClickListeners() {
        mainBinding.btnAdd.setOnClickListener { addNumber() }
        mainBinding.btnRemove.setOnClickListener { removeNumber() }
    }

    private fun removeNumber() {
        if (mainBinding.edNumber.text.isNotEmpty()){
           val deleteContact = ContactEntity(contact = mainBinding.edNumber.text.toString())

            GlobalScope.launch {
                contactsDb.deleteContact(mainBinding.edNumber.text.toString())
                Snackbar.make(mainBinding.root, "Contact Deleted!", Snackbar.LENGTH_SHORT).show()
            }
            mainBinding.edNumber.text.clear()
        }
    }

    private fun addNumber() {
        if (mainBinding.edNumber.text.isNotEmpty()){
            val newContact =  ContactEntity(contact = mainBinding.edNumber.text.toString())
            viewModel.addContact(newContact)
            mainBinding.edNumber.text.clear()
            Snackbar.make(mainBinding.root, "Contact Added!", Snackbar.LENGTH_SHORT).show()

        }
    }

    private fun deleteCallLogByNumber(numberTag: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CALL_LOG, Manifest.permission.WRITE_CONTACTS),
                PERMISSIONS_REQUEST_READ_CONTACTS
            )
            } else {
            val number: CharArray = numberTag.toCharArray()
            var n = "%"
            for (i in number.indices) {
                n = n + (number[i].toString() + "%")
            }
            val queryString = CallLog.Calls.NUMBER + " LIKE '" + n + "'"
            try {
                this.contentResolver.delete(CallLog.Calls.CONTENT_URI, queryString, null)
                //finish()
            }catch (e: Exception){
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                Log.e(TAG, "deleteCallLogByNumber: ${e.message}")
            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_READ_CONTACTS -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "Permission has been denied by user")
                } else {
                    Log.i(TAG, "Permission has been granted by user")
                    
                    for (num in numbers){
                        deleteCallLogByNumber(num)

                    }
                }
            }
        }
    }
}