package com.example.nagwatask.model

sealed class DownloadResult {
    object Success : DownloadResult()
    data class Error(val message: String, val cause: Exception? = null) : DownloadResult()
    data class Progress(val progress: Int,val type:String): DownloadResult()
}