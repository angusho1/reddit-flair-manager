package com.example.reddit_flair_settings

import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.toolbox.StringRequest
import com.example.reddit_flair_settings.models.UserSubreddit
import com.example.reddit_flair_settings.network.RedditAPIService
import com.example.reddit_flair_settings.network.RequestHandler
import kotlinx.android.synthetic.main.activity_flair_settings.*
import org.json.JSONObject

class FlairSettingsActivity : AppCompatActivity() {
    private lateinit var requestHandler: RequestHandler
    private lateinit var subredditListAdapter: SubredditListAdapter
    lateinit var redditAPI: RedditAPIService
    lateinit var subreddits: MutableList<UserSubreddit>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flair_settings)

        requestHandler = RequestHandler.getInstance(applicationContext)
        redditAPI = RedditAPIService(applicationContext)

        val action: String? = intent?.action

        if (isAuthRedirect()) {
            val url: Uri = intent.data as Uri
            handleAuthCodeFlow(url)
//            handleImplicitFlow(url)
        }
    }

    fun initAdapter(subredditItems: MutableList<UserSubreddit>) {
        subredditListAdapter = SubredditListAdapter(subredditItems)

        rvSubredditItems.adapter = subredditListAdapter
        rvSubredditItems.layoutManager = LinearLayoutManager(this)
    }

    fun displaySubreddits() {
        this.redditAPI.getUserSubreddits(
            { response ->
                initAdapter(response)
            },
            { error ->
                Log.d("error", error.localizedMessage)
            }
        )
    }

    private fun handleAuthCodeFlow(uri: Uri) {
        if (uri.getQueryParameter("error") != null) {
            // TODO: handle error
            return
        }
        // TODO: Check state

        val authCode = uri.getQueryParameter("code")
        val tokenUrl = "https://www.reddit.com/api/v1/access_token"

        val appScheme = resources.getString(R.string.app_scheme)
        val appHost = resources.getString(R.string.app_host)
        val redirectUri = "$appScheme://$appHost"

        val postData: MutableMap<String, String> = HashMap()
        postData["grant_type"] = "authorization_code"
        postData["code"] = authCode as String
        postData["redirect_uri"] = redirectUri

        val tokenRequest = object: StringRequest(
            Method.POST, tokenUrl,
            { response ->
                val data = JSONObject(response)
                val accessToken = data.get("access_token")
                val refreshToken = data.get("refresh_token")

                val sp: SharedPreferences = applicationContext.getSharedPreferences("redditPrefs", MODE_PRIVATE)
                val editor = sp.edit()
                editor.putString("accessToken", accessToken as String)
                editor.putString("refreshToken", refreshToken as String)
                editor.apply()

                displaySubreddits()
            },
            { error ->
                Log.d("error", error.localizedMessage)
            }
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
                val clientId = resources.getString(R.string.reddit_app_id)
                val secret = resources.getString(R.string.reddit_app_secret)
                val encoding = encodeBasicAuth(clientId, secret)
                headers.put("Authorization", "Basic $encoding")
                return headers
            }
        }

        requestHandler.addToRequestQueue(tokenRequest)
    }

    private fun encodeBasicAuth(clientId: String, clientSecret: String): String {
        var str = "$clientId:$clientSecret"
        var dataToEncode: ByteArray = str.toByteArray(Charsets.UTF_8)
        return Base64.encodeToString(dataToEncode, Base64.NO_WRAP)
    }

    private fun handleImplicitFlow(uri: Uri) {
        val fragmentParams: MutableMap<String, String> = getFragmentParams(uri)
        // TODO: Check state

        val sp: SharedPreferences = applicationContext.getSharedPreferences("redditPrefs", MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString("accessToken", fragmentParams["access_token"].toString())
        editor.apply()
    }

    private fun isAuthRedirect() : Boolean {
        return intent.data != null
    }

    private fun getFragmentParams(uri: Uri): MutableMap<String, String> {
        val fragment: String = uri.fragment as String
        val args: List<String> = fragment.split("&")
        val fragmentParams: MutableMap<String, String> = hashMapOf()
        for (arg: String in args) {
            var pair: List<String> = arg.split("=")
            fragmentParams[pair[0]] = pair[1]
        }
        return fragmentParams
    }

}