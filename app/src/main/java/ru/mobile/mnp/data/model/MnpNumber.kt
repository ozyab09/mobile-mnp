package ru.mobile.mnp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mnp_numbers")
data class MnpNumber(
    @PrimaryKey
    val number: String,
    val operator: String
)