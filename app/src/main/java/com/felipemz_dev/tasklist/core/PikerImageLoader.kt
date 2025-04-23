package com.felipemz_dev.tasklist.core

import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import com.felipemz_dev.tasklist.R

class PikerImageLoader {

    companion object {
        fun showPicker(
            context: Context,
            onStart: (chooserIntent: Intent) -> Unit
        ) {
            val pickImageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val chooserIntent = Intent.createChooser(
                pickImageIntent,
                context.getString(R.string.select_image)
            )
            chooserIntent.putExtra(
                Intent.EXTRA_INITIAL_INTENTS,
                if (pickImageIntent.resolveActivity(context.packageManager) != null) {
                    arrayOf(pickImageIntent, takePhotoIntent)
                } else {
                    arrayOf(takePhotoIntent)
                }
            )
            onStart(chooserIntent)
        }
    }
}