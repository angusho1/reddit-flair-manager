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
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        redditAPI = RedditAPIService(requireActivity().applicationContext)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

    fun navigateToFlairOptions(subreddit: UserSubreddit) {
        if (subreddit.flairsEnabled) {
            val bundle = bundleOf("subredditName" to subreddit.name)
            findNavController().navigate(R.id.action_SubbredditList_to_FlairList, bundle)
        }
    }

    private fun initAdapter(subredditItems: MutableList<UserSubreddit>) {
        subredditListAdapter = SubredditListAdapter(subredditItems, ::navigateToFlairOptions)

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