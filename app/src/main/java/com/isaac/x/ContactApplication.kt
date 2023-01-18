package com.isaac.x

import android.app.Application
import com.isaac.x.ContactDatabase

class ContactApplication : Application() {
    val database: ContactDatabase by lazy{ ContactDatabase.getDatabase(this)}
}