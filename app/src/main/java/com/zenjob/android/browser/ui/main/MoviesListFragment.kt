package com.zenjob.android.browser.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zenjob.android.browser.R
import com.zenjob.android.browser.data.Movie
import com.zenjob.android.browser.data.NetworkHelper
import com.zenjob.android.browser.data.repository.NetworkState
import com.zenjob.android.browser.data.repository.TMDBApi
import kotlinx.android.synthetic.main.main_fragment_list.*


/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [MoviesListFragment.OnListFragmentInteractionListener] interface.
 */
class MoviesListFragment : Fragment(), MovieListAdapter.OnItemClickListener {

    // TODO: Customize parameters
    private var columnCount = 1
    private lateinit var viewModel: MoviesViewModel
    private lateinit var navController: NavController
    lateinit var movieRepository: MoviePagedListRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.main_fragment_list, container, false)
        val apiService: TMDBApi = NetworkHelper.tmdbApi()
        movieRepository = MoviePagedListRepository(apiService)

        val list: RecyclerView = view.findViewById(R.id.list)
        val movieAdapter = MovieListAdapter().apply { listener = this@MoviesListFragment }

        val llm = LinearLayoutManager(activity)

        llm.orientation = LinearLayoutManager.VERTICAL
        list.layoutManager = llm
        list.setHasFixedSize(true)
        list.adapter = movieAdapter

        viewModel = getViewModel()

        viewModel.moviePagedList.observe(this, Observer {
            movieAdapter.submitList(it)
        })

        viewModel.networkState.observe(this, Observer {
            progress_bar_popular.visibility =
                if (viewModel.listIsEmpty() && it == NetworkState.LOADING) View.VISIBLE else View.GONE
            txt_error_popular.visibility =
                if (viewModel.listIsEmpty() && it == NetworkState.ERROR) View.VISIBLE else View.GONE

            if (!viewModel.listIsEmpty()) {
                movieAdapter.setNetworkState(it)
            }
        })
        navController = Navigation.findNavController(
            requireActivity(), R.id.nav_host
        )
        val refresh = view.findViewById<View>(R.id.refresh)
        refresh.setOnClickListener {
            viewModel.refreshData()
        }

        return view
    }


    override fun onDetach() {
        super.onDetach()
    }


    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            MoviesListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }

    override fun onMovieItemClick(itemView: View, position: Int, movie: Movie) {
        var bundle = bundleOf("movie_id" to movie.id)
        navController.navigate(R.id.action_mainFragment_to_detailsFragment, bundle)
        navController.popBackStack(R.id.action_mainFragment_to_detailsFragment, true);
    }

    private fun getViewModel(): MoviesViewModel {
        return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MoviesViewModel(
                    movieRepository
                ) as T
            }
        })[MoviesViewModel::class.java]
    }
}
