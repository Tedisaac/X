package com.isaac.x

import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.CallLog
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.isaac.x.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_READ_CONTACTS = 100

    private val numbers = ArrayList<String>()

    private val mainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mainBinding.root)

        supportActionBar?.hide()

        setClickListeners()

        for (num in numbers){

            Log.e(TAG, "onCreate: $num", )
            
            //deleteCallLogByNumber(num)
        }


    }

    private fun setClickListeners() {
        mainBinding.btnAdd.setOnClickListener { addNumber() }
        mainBinding.btnRemove.setOnClickListener { removeNumber() }
    }

    private fun removeNumber() {
        if (mainBinding.edNumber.text.isNotEmpty()){
            for (num in numbers){
                if (mainBinding.edNumber.text.toString().equals(num)){
                    numbers.remove(num)

                    mainBinding.edNumber.text.clear()

                    Log.e(TAG, "removeNumber: ${numbers.size}", )
                }
            }
        }
    }

    private fun addNumber() {
        if (mainBinding.edNumber.text.isNotEmpty()){
            numbers.add(mainBinding.edNumber.text.toString())

            mainBinding.edNumber.text.clear()

            Log.e(TAG, "addNumber: ${numbers.size}", )
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

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
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