package com.example.reddit_flair_manager

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reddit_flair_manager.adapters.FlairListAdapter
import com.example.reddit_flair_manager.adapters.SubredditListAdapter
import com.example.reddit_flair_manager.databinding.FragmentFlairListBinding
import com.example.reddit_flair_manager.models.UserFlair
import com.example.reddit_flair_manager.models.UserSubreddit
import com.example.reddit_flair_manager.network.RedditAPIService
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_flair_list.*
import kotlinx.android.synthetic.main.fragment_subreddit_list.*


/**
 * A simple [Fragment] subclass.
 * Use the [FlairListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FlairListFragment : Fragment() {
    private var _binding: FragmentFlairListBinding? = null
    private lateinit var subredditName: String
    private lateinit var subreddit: UserSubreddit
    private lateinit var flairListAdapter: FlairListAdapter
    private lateinit var redditAPI: RedditAPIService

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        arguments?.let {
            subredditName = it.getString("subredditName").toString()
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            Log.e("adsf", "fdaf")
        }

        (activity as FlairManagerActivity).setActionBarTitle(subredditName)

        redditAPI = RedditAPIService(requireActivity().applicationContext)

        displayFlairOptions()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFlairListBinding.inflate(inflater, container, false)
        binding.indeterminateBar.visibility = View.VISIBLE

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_flair, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_save_flair) {
            saveFlair()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FlairListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FlairListFragment().apply {
                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun displayFlairOptions() {
        this.redditAPI.getSubredditFlairs(subredditName,
            { response ->
                initAdapter(response)
                binding.indeterminateBar.visibility = View.GONE
            },
            { error ->
                Log.e("Flair Request", error.message.toString())
            }
        )
    }

    private fun initAdapter(flairOptions: MutableList<UserFlair>) {
        flairListAdapter = FlairListAdapter(flairOptions)

        rvFlairOptions.adapter = flairListAdapter
        rvFlairOptions.layoutManager = LinearLayoutManager(requireActivity())
    }

    fun saveFlair() {
        val pos = flairListAdapter.checkedPosition
        if (pos != -1) {
            val flair = flairListAdapter.flairOptions[pos]
            redditAPI.updateUserFlair(subredditName, flair, {
                findNavController().navigate(R.id.action_FlairList_to_SubbredditList)
                Snackbar.make(requireView(), "$subredditName flair updated successfully", Snackbar.LENGTH_LONG).show()
            },
            { error ->
                Snackbar.make(requireView(), "Failed to update flair", Snackbar.LENGTH_LONG).show()
            })
            flairListAdapter
        }
    }
}