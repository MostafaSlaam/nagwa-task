package com.example.nagwatask.util

import android.R.attr
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.example.nagwatask.BuildConfig
import com.example.nagwatask.model.DownloadResult
import com.example.nagwatask.ui.main.ItemsState
import io.reactivex.Observable
import kotlinx.coroutines.flow.*
import retrofit2.Response
import java.net.URL
import java.io.*
import java.net.HttpURLConnection


object Utils {
    fun <T : Any> handleApiError(resp: Response<T>): AppResult.Error {
        val error = ApiErrorUtils.parseError(resp)
        return AppResult.Error(Exception(error.message))
    }

    fun <T : Any> handleSuccess(response: Response<T>): AppResult<T> {
        response.body()?.let {
            return AppResult.Success(it)
        } ?: return handleApiError(response)
    }



    suspend fun downloadUsingConnection(file: File, urlString: String): Flow<DownloadResult> {
        return flow {
            var output: OutputStream? = null
            var input: InputStream? = null
            var connection: HttpURLConnection? = null
            var progress = 0
            try {
                emit(DownloadResult.Progress(0,""))
                val url = URL(urlString)
                connection = url.openConnection() as HttpURLConnection?

                connection!!.connect()
                val fileLength = connection.contentLength
                input = connection.inputStream
                output = FileOutputStream(file.path)

                val data = ByteArray(1024)
                var total: Int = 0
                var count: Int = 0
                while ((input.read(data).also { count = it }) > 0) {

                    output.write(data, 0, count)
                    total += count
                    if (fileLength != -1)
                        emit(
                            DownloadResult.Progress((total * 100 / fileLength),"%")
                        )
                    else
                        emit(DownloadResult.Progress(0,"Byte"))


                }
            } catch (e: Exception) {
                emit(DownloadResult.Error("File not downloaded"))
            } finally {
                try {
                    if (output != null) output.close()
                    if (input != null) input.close()
                } catch (ignored: IOException) {
                }

                if (connection != null) connection.disconnect()
                emit(DownloadResult.Success)
            }

        }
    }

     fun downloadUsingConnectionRx(file: File, urlString: String): Observable<DownloadResult> {
        return Observable.create {emitter ->
            var output: OutputStream? = null
            var input: InputStream? = null
            var connection: HttpURLConnection? = null
            var progress = 0
            try {
                emitter.onNext(DownloadResult.Progress(0,""))
                val url = URL(urlString)
                connection = url.openConnection() as HttpURLConnection?

                connection!!.connect()
                val fileLength = connection.contentLength
                input = connection.inputStream
                output = FileOutputStream(file.path)

                val data = ByteArray(1024)
                var total: Int = 0
                var count: Int = 0
                while ((input.read(data).also { count = it }) > 0) {

                    output.write(data, 0, count)
                    total += count
                    if (fileLength != -1)
                        emitter.onNext(
                            DownloadResult.Progress((total * 100 / fileLength),"%")
                        )
                    else
                        emitter.onNext(DownloadResult.Progress(0,"Byte"))


                }
            } catch (e: Exception) {
                emitter.onNext(DownloadResult.Error("File not downloaded"))
            } finally {
                try {
                    if (output != null) output.close()
                    if (input != null) input.close()
                } catch (ignored: IOException) {
                }

                if (connection != null) connection.disconnect()
                emitter.onNext(DownloadResult.Success)
            }

        }
    }



    fun Activity.openFile(file: File) {
        Intent(Intent.ACTION_VIEW).apply {
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            addCategory(Intent.CATEGORY_DEFAULT)
            val uri = FileProvider.getUriForFile(
                this@openFile,
                BuildConfig.APPLICATION_ID + ".provider",
                file
            )
            val mimeType = getMimeType(file)
            mimeType?.let {
                setDataAndType(uri, it)
                startActivity(this)
            }

        }
    }

    fun getMimeType(file: File): String? {
        val extension = file.extension
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }
}