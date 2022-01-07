package com.example.reddit_flair_settings

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reddit_flair_settings.adapters.SubredditListAdapter
import com.example.reddit_flair_settings.models.UserSubreddit
import com.example.reddit_flair_settings.network.RedditAPIService
import kotlinx.android.synthetic.main.activity_flair_settings.*

class FlairSettingsActivity : AppCompatActivity() {
    private lateinit var subredditListAdapter: SubredditListAdapter
    lateinit var redditAPI: RedditAPIService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flair_settings)

        redditAPI = RedditAPIService(applicationContext)

//        val action: String? = intent?.action

        if (isAuthRedirect()) {
            val url: Uri = intent.data as Uri
            handleAuthCodeFlow(url)
//            handleImplicitFlow(url)
        } else {
            displaySubreddits()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sign_out -> {
                redditAPI.signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initAdapter(subredditItems: MutableList<UserSubreddit>) {
        subredditListAdapter = SubredditListAdapter(subredditItems)

        rvSubredditItems.adapter = subredditListAdapter
        rvSubredditItems.layoutManager = LinearLayoutManager(this)
    }

    private fun displaySubreddits() {
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
        redditAPI.retrieveAccessToken(
            uri,
            {
                displaySubreddits()
            },
            { error ->
                Log.d("error", error.localizedMessage)
            }
        )
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