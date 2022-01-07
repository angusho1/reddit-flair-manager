package com.example.reddit_flair_settings.network

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.reddit_flair_settings.R
import org.json.JSONObject

// Reference: https://developer.android.com/training/volley/requestqueue#singleton

class RequestHandler constructor(context: Context) {
    companion object {
        @Volatile
        private var INSTANCE: RequestHandler? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: RequestHandler(context).also {
                    INSTANCE = it
                }
            }
    }

    val requestQueue: RequestQueue by lazy {
        // applicationContext is key, it keeps you from leaking the
        // Activity or BroadcastReceiver if someone passes one in.
        Volley.newRequestQueue(context.applicationContext)
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }

    fun sendAuthorizedRequest(applicationContext: Context, method: Int, url: String, jsonRequest: JSONObject?, listener: Response.Listener<JSONObject>, errorListener: Response.ErrorListener) {
        val sp: SharedPreferences = applicationContext.getSharedPreferences("redditPrefs",
            AppCompatActivity.MODE_PRIVATE
        )
        val accessToken = sp.getString("accessToken", "").toString()

        val jsonObjectRequest = object: JsonObjectRequest(
            method,
            url,
            jsonRequest,
            listener,
            errorListener
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer $accessToken")
                return headers
            }
        }

        addToRequestQueue(jsonObjectRequest)
    }

    fun sendBasicAuthRequest(applicationContext: Context, method: Int, url: String, postData: MutableMap<String, String>, listener: Response.Listener<String>,errorListener: Response.ErrorListener) {
        val stringRequest = object: StringRequest(
            Method.POST,
            url,
            listener,
            errorListener
        ) {
            override fun getBodyContentType(): String {
                return "application/x-www-form-urlencoded; charset=UTF-8"
            }

            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                return postData
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                val clientId = applicationContext.resources.getString(R.string.reddit_app_id)
                val secret = applicationContext.resources.getString(R.string.reddit_app_secret)
                val encoding = encodeBasicAuth(clientId, secret)
                headers.put("Authorization", "Basic $encoding")
                return headers
            }
        }

        addToRequestQueue(stringRequest)
    }

    private fun encodeBasicAuth(clientId: String, clientSecret: String): String {
        var str = "$clientId:$clientSecret"
        var dataToEncode: ByteArray = str.toByteArray(Charsets.UTF_8)
        return Base64.encodeToString(dataToEncode, Base64.NO_WRAP)
    }

}