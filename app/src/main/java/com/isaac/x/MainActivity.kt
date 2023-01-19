package com.isaac.x

import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.ConfigurationInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.CallLog
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.isaac.x.adapters.ContactAdapter
import com.isaac.x.databinding.ActivityMainBinding
import com.isaac.x.models.ContactEntity
import com.isaac.x.viewmodels.ContactViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var adapter: ContactAdapter

    private val PERMISSIONS_REQUEST_READ_CONTACTS = 100

    private val numbers: ArrayList<ContactEntity> = ArrayList()
    private lateinit var contact: String
    private lateinit var newContact: ContactEntity

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

        numbers.clear()

        setViews()

        setClickListeners()

        setUpRecyclerView()
        
        fetchContacts()

    }

    private fun setViews() {
        val nightModeFlags: Int? = applicationContext.resources.configuration.uiMode?.and(
            Configuration.UI_MODE_NIGHT_MASK
        )
        when(nightModeFlags){
            Configuration.UI_MODE_NIGHT_NO -> {
                mainBinding.btnAdd.background = resources.getDrawable(R.drawable.btn_background)
                mainBinding.btnRemove.background = resources.getDrawable(R.drawable.btn_background)
                mainBinding.edNumber.setHintTextColor(Color.GRAY)
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                mainBinding.btnAdd.background = resources.getDrawable(R.drawable.btn_background_light)
                mainBinding.btnRemove.background = resources.getDrawable(R.drawable.btn_background_light)
                mainBinding.btnAdd.setTextColor(Color.BLACK)
                mainBinding.btnRemove.setTextColor(Color.BLACK)
                mainBinding.edNumber.setHintTextColor(Color.WHITE)
            }
        }
    }

    private fun deleteLogs() {
        if (numbers.isNotEmpty()){
            for (num in numbers){
                deleteCallLogByNumber(num.contact)
            }
        }
    }

    private fun setUpRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        mainBinding.rvContacts.layoutManager = layoutManager
    }

    private fun fetchContacts() {
        mainBinding.btnAdd.isEnabled = false
        mainBinding.btnAdd.isClickable = false
        mainBinding.btnRemove.isEnabled = false
        mainBinding.btnRemove.isClickable = false
       viewModel.allContacts.observe(this){result ->

           if (result.isNotEmpty()){
               adapter = ContactAdapter(result)
               mainBinding.rvContacts.adapter = adapter
               mainBinding.rvContacts.adapter?.notifyDataSetChanged()

               for(contact in result){
                   numbers.add(contact)
               }

               deleteLogs()

               mainBinding.btnAdd.isEnabled = true
               mainBinding.btnAdd.isClickable = true
               mainBinding.btnRemove.isEnabled = true
               mainBinding.btnRemove.isClickable = true
           }else{
               adapter = ContactAdapter(result)
               mainBinding.rvContacts.adapter = adapter
               mainBinding.rvContacts.adapter?.notifyDataSetChanged()
               viewModel.showSnackBar(mainBinding.root, "No Contacts Available!")
               mainBinding.btnAdd.isEnabled = true
               mainBinding.btnAdd.isClickable = true
               mainBinding.btnRemove.isEnabled = true
               mainBinding.btnRemove.isClickable = true
           }

       }
    }

    private fun setClickListeners() {
        mainBinding.btnAdd.setOnClickListener { addNumber() }
        mainBinding.btnRemove.setOnClickListener { removeNumber() }
    }

    private fun removeNumber() {
        if (mainBinding.edNumber.text.isNotEmpty()){
            if (mainBinding.edNumber.text.startsWith("254") && mainBinding.edNumber.text.length != 12
                || mainBinding.edNumber.text.startsWith("0") && mainBinding.edNumber.text.length != 10
            ){
               viewModel.showSnackBar(mainBinding.root, "Please Input a Valid Contact!!")
            }else{
                if (numbers.isNotEmpty()){
                    for (number in numbers){
                        contact = number.contact
                        if (contact == mainBinding.edNumber.text.toString()){
                            contact = mainBinding.edNumber.text.toString()
                        }else{
                            viewModel.showSnackBar(mainBinding.root, "Contact Does Not Exist")
                        }
                    }
                    viewModel.deleteContact(contact)
                    viewModel.showSnackBar(mainBinding.root, "Contact Deleted!")

                    mainBinding.edNumber.text.clear()
                }else{
                    viewModel.showSnackBar(mainBinding.root, "No Contacts!!")
                }
            }


        }else{
            viewModel.showSnackBar(mainBinding.root, "Please Input Contact First!!")
        }
    }

    private fun addNumber() {
        if (mainBinding.edNumber.text.isNotEmpty()){
            if (mainBinding.edNumber.text.startsWith("254") && mainBinding.edNumber.text.length != 12
                || mainBinding.edNumber.text.startsWith("0") && mainBinding.edNumber.text.length != 10
            ){
                viewModel.showSnackBar(mainBinding.root, "Please Input a Valid Contact!!")
            }else{
                if(numbers.size > 0){
                    for (num in numbers){
                        if ((num.contact) != mainBinding.edNumber.text.toString()){
                            newContact =  ContactEntity(contact = mainBinding.edNumber.text.toString())
                        } else{
                            viewModel.showSnackBar(mainBinding.root, "Contact Already Exists!!")
                        }

                    }
                    viewModel.addContact(newContact)
                    mainBinding.edNumber.text.clear()
                    viewModel.showSnackBar(mainBinding.root, "Contact Added!")
                    deleteLogs()
                }else{
                    newContact =  ContactEntity(contact = mainBinding.edNumber.text.toString())
                    viewModel.addContact(newContact)
                    mainBinding.edNumber.text.clear()
                    viewModel.showSnackBar(mainBinding.root, "Contact Added!")
                    deleteLogs()
                }


            }
        }else{
            viewModel.showSnackBar(mainBinding.root, "Please Input Contact First!!")
        }
    }

    private fun deleteCallLogByNumber(numberTag: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CALL_LOG, Manifest.permission.WRITE_CONTACTS),
                PERMISSIONS_REQUEST_READ_CONTACTS
            )
            } else {
                viewModel.deleteCallLog(mainBinding.root,this, numberTag)
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
                    fetchContacts()
                }
            }
        }
    }
}