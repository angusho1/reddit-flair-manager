package com.example.reddit_flair_manager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import com.example.reddit_flair_manager.network.RedditAPIService
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    lateinit var redditAPI: RedditAPIService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_login)

        redditAPI = RedditAPIService(applicationContext)

        sign_in_button.setOnClickListener {
            startActivity(redditAPI.getOAuthSignInIntent())
        }
    }

}