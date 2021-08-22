package com.example.nagwatask.model

import android.os.Environment
import java.io.File

class ItemModel(
    val id: String = "",
    val type: String = "",
    val url: String = "",
    val name: String = "",
    var isLoading: Boolean = false,
    var progress:Int=0
)
