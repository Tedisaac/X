package com.isaac.x.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey (autoGenerate = true) var id: Int = 0,
    @ColumnInfo (name = "contact") var contact: String
)
