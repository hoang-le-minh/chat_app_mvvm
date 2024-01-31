package com.android.hoang.chatapplication.ui.chat

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore

class ImagesGallery {
    companion object {
        fun listOfImages(context: Context): ArrayList<String>{
            val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val cursor: Cursor
            var columnIndexData: Int
            var columnIndexFolderName: Int
            val listOfAllImages = arrayListOf<String>()
            var absolutePathOfImage: String


            val projection = arrayOf(
                MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
            )

            val orderBy = MediaStore.Video.Media.DATE_TAKEN
            cursor = context.contentResolver.query(uri, projection, null, null, "$orderBy DESC")!!
            columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)

            // get folder name
            // columnIndexFolderName = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME)

            while (cursor.moveToNext()){
                absolutePathOfImage = cursor.getString(columnIndexData)
                listOfAllImages.add(absolutePathOfImage)
            }

            return listOfAllImages
        }
    }
}