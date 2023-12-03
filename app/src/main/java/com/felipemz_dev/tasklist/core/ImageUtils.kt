package com.felipemz_dev.tasklist.core

import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ImageUtils {
    companion object {
        fun saveBitmapToDirectory(bitmap: Bitmap): String? {
            if (!isExternalStorageWritable()) return null
            val directory = getTaskImagesDirectory()

            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "IMG_$timeStamp.jpg"

            val file = File(directory, fileName)

            return try {
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                }
                file.absolutePath
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }

        private fun isExternalStorageWritable(): Boolean {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state
        }

        private fun getTaskImagesDirectory(): File {
            val directory = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "taskImages"
            )
            if (!directory.exists()) directory.mkdirs()
            return directory
        }

        fun containsAbsolutePath(text: String): Boolean {
            val picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val absolutePath = picturesDirectory.absolutePath
            return text.startsWith(absolutePath)
        }

        fun isUriValid(uri: Uri): Boolean {
            return try {
                val file = uri.path?.let { File(it) }
                file!!.exists()
            } catch (e: Exception) {
                false
            }
        }
    }
}