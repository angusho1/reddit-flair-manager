package com.example.reddit_flair_settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.reddit_flair_settings.databinding.FragmentFirstBinding
import android.content.Intent
import android.net.Uri


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonFirst.setOnClickListener {
            this.openOAuthSignIn()
//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openOAuthSignIn() {
        val clientId = getString(R.string.reddit_app_id)
        val appScheme = resources.getString(R.string.app_scheme)
        val appHost = resources.getString(R.string.app_host)
        val redirectUri = "$appScheme://$appHost"
        val state = "RANDOM"
        val responseType = "code"
        val scopeStr = "identity flair mysubreddits"
        val baseUrl = "https://www.reddit.com/api/v1/authorize.compact"
        val authUrl = "$baseUrl?client_id=$clientId&response_type=$responseType&state=$state&redirect_uri=$redirectUri&scope=$scopeStr&duration=permanent"
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(authUrl))
        startActivity(browserIntent)
    }
}