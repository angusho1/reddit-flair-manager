package com.example.reddit_flair_settings

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.example.reddit_flair_settings.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefFileKey = resources.getString(R.string.preference_file_key)
        val sp: SharedPreferences = applicationContext.getSharedPreferences(prefFileKey, MODE_PRIVATE)

        val launchIntent: Intent
        val refreshToken = sp.getString("refreshToken", null)
        launchIntent = if (refreshToken == null) {
            Intent(this, LoginActivity::class.java)
        } else {
            Intent(this, FlairSettingsActivity::class.java)
        }
        startActivity(launchIntent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}