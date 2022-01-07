package com.example.reddit_flair_settings.network

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.example.reddit_flair_settings.R
import com.example.reddit_flair_settings.models.*
import org.json.JSONObject
import java.net.URL

class RedditAPIService constructor(context: Context) {
    private val applicationContext: Context = context
    private val requestHandler: RequestHandler = RequestHandler.getInstance(context)
    private val baseUrl = context.resources.getString(R.string.api_base_url)
    private val appScheme = applicationContext.getString(R.string.app_scheme)
    private val appHost = applicationContext.getString(R.string.app_host)
    private val clientId = applicationContext.getString(R.string.reddit_app_id)
    private lateinit var appState: String

    fun getOAuthSignInIntent(): Intent {
        val redirectUri = "$appScheme://$appHost"
        appState = "RANDOM"
        val responseType = "code"
        val scopeStr = "identity flair mysubreddits"
        val baseUrl = "https://www.reddit.com/api/v1/authorize.compact"
        val authUrl =
            "$baseUrl?client_id=$clientId&response_type=$responseType&state=$appState&redirect_uri=$redirectUri&scope=$scopeStr&duration=permanent"
        return Intent(Intent.ACTION_VIEW, Uri.parse(authUrl))
    }

    fun retrieveAccessToken(uri: Uri, cb: () -> Unit, errorListener: Response.ErrorListener) {
        if (uri.getQueryParameter("error") != null) {
            // TODO: handle error
            return
        }
//        if (uri.getQueryParameter("state") != appState) {
//            // TODO: handle error
//            return
//        }

        appState = ""

        val authCode = uri.getQueryParameter("code")
        val tokenUrl = applicationContext.getString(R.string.access_token_url)
        val redirectUri = "$appScheme://$appHost"

        val postData: MutableMap<String, String> = HashMap()
        postData["grant_type"] = "authorization_code"
        postData["code"] = authCode as String
        postData["redirect_uri"] = redirectUri

        requestHandler.sendBasicAuthRequest(
            applicationContext,
            Request.Method.POST,
            tokenUrl,
            postData,
            { response ->
                val data = JSONObject(response)
                val accessToken = data.get("access_token")
                val refreshToken = data.get("refresh_token")
                val expiresIn = (data.get("expires_in") as Int).toLong()

                val prefFileKey = applicationContext.resources.getString(R.string.preference_file_key)
                val sp: SharedPreferences = applicationContext.getSharedPreferences(prefFileKey, AppCompatActivity.MODE_PRIVATE)
                val editor = sp.edit()
                editor.putString("accessToken", accessToken as String)
                editor.putString("refreshToken", refreshToken as String)

                val currTime = System.currentTimeMillis() / 1000

                editor.putLong("accessTokenExpiryTime", expiresIn + currTime)
                editor.apply()

                cb()
            },
            errorListener
        )
    }

    fun signOut() {
        val prefFileKey = applicationContext.resources.getString(R.string.preference_file_key)
        val sp: SharedPreferences = applicationContext.getSharedPreferences(prefFileKey, AppCompatActivity.MODE_PRIVATE)

        val refreshToken = sp.getString("refreshToken", null)

        if (refreshToken != null) {
            revokeToken(refreshToken, "refresh_token")
        }
    }

    fun getUserIdentity(listener: Response.Listener<JSONObject>, errorListener: Response.ErrorListener) {
        val url = "$baseUrl/api/v1/me"

        val sendRequest = {
            requestHandler.sendAuthorizedRequest(applicationContext, Request.Method.GET, url, null,
                { response ->
                    listener.onResponse(response)
                }, errorListener)
        }

        ensureAccessToken(sendRequest)
    }

    fun getUserSubreddits(cb: (x: MutableList<UserSubreddit>) -> Unit, errorListener: Response.ErrorListener) {
        val url = "$baseUrl/subreddits/mine/subscriber"

        val sendRequest = {
            requestHandler.sendAuthorizedRequest(applicationContext, Request.Method.GET, url, null,
                { response ->
                    val subreddits = response.getJSONObject("data").getJSONArray("children")
                    val res: MutableList<UserSubreddit> = mutableListOf()

                    for (i in 0 until subreddits.length()) {
                        val subData = subreddits.getJSONObject(i).getJSONObject("data")
                        var richText: MutableList<FlairRichTextComponent>?

                        val richTextComponents = subData.getJSONArray("user_flair_richtext")

                        val flairType: String = subData.getString("user_flair_type")

                        if (flairType == "richtext") {
                            richText = mutableListOf()
                            for (j in 0 until richTextComponents.length()) {
                                val component = richTextComponents.getJSONObject(j)
                                val type = component.getString("e")
                                if (type == "text") {
                                    val text = component.getString("t")
                                    richText.add(TextComponent(text))
                                } else if (type == "emoji") {
                                    val id = component.getString("a")
                                    val url = component.getString("u")
                                    richText.add(IconComponent(id, url))
                                }
                            }
                        } else {
                            richText = null
                        }

                        val userFlair = UserFlair(
                            subData.getString("user_flair_background_color"),
                            subData.getString("user_flair_text_color"),
                            flairType,
                            richText
                        )

                        val iconImg = subData.getString("icon_img")
                        var iconUrl: URL?
                        if (iconImg == "") {
                            iconUrl = null
                        } else {
                            iconUrl = URL(iconImg)
                        }
                        val subreddit = UserSubreddit(
                            subData.getString("display_name_prefixed"),
                            iconUrl,
                            subData.getBoolean("can_assign_user_flair"),
                            userFlair
                        )

                        res.add(subreddit)
                    }

                    cb(res)

                }, errorListener
            )
        }

        ensureAccessToken(sendRequest)
    }

    private fun ensureAccessToken(cb: () -> Unit) {
        val prefFileKey = applicationContext.resources.getString(R.string.preference_file_key)
        val sp: SharedPreferences = applicationContext.getSharedPreferences(prefFileKey, AppCompatActivity.MODE_PRIVATE)

        val currTime = System.currentTimeMillis() / 1000
        val expiryTime = sp.getLong("accessTokenExpiryTime", currTime)
        val accessToken = sp.getString("accessToken", null)

        if (currTime < expiryTime - 5 && accessToken != null) {
            cb()
            return
        }

        val postData: MutableMap<String, String> = HashMap()
        postData["grant_type"] = "refresh_token"
        postData["refresh_token"] = sp.getString("refreshToken", "").toString()

        requestHandler.sendBasicAuthRequest(
            applicationContext,
            Request.Method.POST,
            applicationContext.getString(R.string.access_token_url),
            postData,
            { response ->
                val data = JSONObject(response)
                val newAccessToken = data.get("access_token")
                val expiresIn = (data.get("expires_in") as Int).toLong()

                val editor = sp.edit()
                editor.putString("accessToken", newAccessToken as String)

                val newCurrTime = System.currentTimeMillis() / 1000

                editor.putLong("accessTokenExpiryTime", expiresIn + newCurrTime)
                editor.apply()

                cb()
            },
            { error ->
                Log.e("Access Token Error", error.message.toString())
            }
        )

        if (accessToken != null) {
            revokeToken(accessToken, "access_token")
        }
    }

    private fun revokeToken(token: String, type: String) {
        val postData: MutableMap<String, String> = HashMap()
        postData["token"] = token
        postData["token_type_hint"] = type

        requestHandler.sendBasicAuthRequest(
            applicationContext,
            Request.Method.POST,
            applicationContext.getString(R.string.revoke_token_url),
            postData,
            { response ->
                Log.d("Success", "$type revoked")
                val prefFileKey = applicationContext.resources.getString(R.string.preference_file_key)
                val sp: SharedPreferences = applicationContext.getSharedPreferences(prefFileKey, AppCompatActivity.MODE_PRIVATE)
                val editor = sp.edit()

                if (type == "access_token") {
                    val storedAccessToken = sp.getString("accessToken", "")
                    if (storedAccessToken == token) {
                        editor.remove("accessToken")
                    }
                } else if (type == "refresh_token") {
                    editor.remove("refreshToken")
                    editor.remove("accessToken")
                    editor.remove("accessTokenExpiryTime")
                }
                editor.apply()
            },
            { error ->
                Log.e("Token revocation", error.message.toString())
            }
        )

    }
}