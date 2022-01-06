package com.example.reddit_flair_settings.utils

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.widget.ImageView
import java.lang.Exception
import java.net.URL

// Ref: https://www.tutorialspoint.com/how-do-i-load-an-imageview-by-url-on-android-using-kotlin

@SuppressLint("StaticFieldLeak")
@Suppress("DEPRECATION")
class InternetImageLoader(var imageView: ImageView) : AsyncTask<String, Void, Bitmap?>() {

    override fun doInBackground(vararg urls: String): Bitmap? {
        val imageURL = urls[0]
        var image: Bitmap? = null
        try {
            val stream = URL(imageURL).openStream()
            image = BitmapFactory.decodeStream(stream)
        }
        catch (e: Exception) {
            Log.e("Error Message", e.message.toString())
            e.printStackTrace()
        }
        return image
    }
    override fun onPostExecute(result: Bitmap?) {
        imageView.setImageBitmap(result)
    }
}