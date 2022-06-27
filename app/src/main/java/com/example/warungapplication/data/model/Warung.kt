package com.example.warungapplication.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Warung (

    var warungId : String = "",
    var name : String = "",
    var address : String = "",
    var location : String= "",
    var imgWarung : String = "",
) : Parcelable
