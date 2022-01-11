package com.example.reddit_flair_manager

import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reddit_flair_manager.adapters.SubredditListAdapter
import com.example.reddit_flair_manager.databinding.FragmentSubredditListBinding
import com.example.reddit_flair_manager.models.UserSubreddit
import com.example.reddit_flair_manager.network.RedditAPIService
import kotlinx.android.synthetic.main.fragment_subreddit_list.*

class SubredditListFragment : Fragment() {

    private var _binding: FragmentSubredditListBinding? = null
    private lateinit var subredditListAdapter: SubredditListAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var redditAPI: RedditAPIService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        redditAPI = RedditAPIService(requireActivity().applicationContext)

        _binding = FragmentSubredditListBinding.inflate(inflater, container, false)
        if (isAuthRedirect()) {
            val url: Uri = requireActivity().intent.data as Uri
            handleAuthCodeFlow(url)
        } else {
            displaySubreddits()
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initAdapter(subredditItems: MutableList<UserSubreddit>) {
        subredditListAdapter = SubredditListAdapter(subredditItems)

        rvSubredditItems.adapter = subredditListAdapter
        rvSubredditItems.layoutManager = LinearLayoutManager(requireActivity())
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
                redditAPI.getUserIdentity(
                    { user ->
                        val prefFileKey = resources.getString(R.string.preference_file_key)
                        val sp: SharedPreferences = requireActivity().getSharedPreferences(prefFileKey,
                            AppCompatActivity.MODE_PRIVATE
                        )
                        val editor = sp.edit()
                        editor.putString("username", user.name)
                        editor.apply()
                    },
                    { error ->
                        Log.d("error", error.localizedMessage)
                    }
                )
            },
            { error ->
                Log.d("error", error.localizedMessage)
            }
        )
    }

    private fun isAuthRedirect() : Boolean {
        return requireActivity().intent.data != null
    }
}