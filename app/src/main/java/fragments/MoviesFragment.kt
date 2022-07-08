package fragments

import adapters.MoviesAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.omega.moviedatabase.R
import network.models.Movie
import repositories.MoviesRepo
import viewmodels.MoviesViewModel
import viewmodels.MoviesViewModelFactory

class MoviesFragment : Fragment() {

    lateinit var moviesRecycler: RecyclerView
    lateinit var progress: ProgressBar
    var genreName: String = ""
    var genreId: Int = 0
    var page = 1

    lateinit var adapter: MoviesAdapter
    lateinit var layoutManager: LinearLayoutManager
    var moviesList: MutableList<Movie> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movies, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getViews(view)

        arguments?.getString("name")?.let {
            genreName = it
        }
        arguments?.getInt("id")?.let {
            genreId = it
        }

        // Recycler View UI Settings
        layoutManager = GridLayoutManager(context, 2)
        moviesRecycler.layoutManager = layoutManager
        adapter = MoviesAdapter(moviesList)
        moviesRecycler.adapter = adapter

        // ViewModel Implementation
        val moviesViewModelFactory = MoviesViewModelFactory(MoviesRepo())
        val moviesViewModel =
            ViewModelProvider(this, moviesViewModelFactory)[MoviesViewModel::class.java]
        moviesViewModel.getMovies(genreId, 1)


        moviesViewModel.movies.observeForever {
            moviesList.addAll(it)
            adapter.notifyDataSetChanged()
            progress.visibility = View.GONE
            moviesRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!recyclerView.canScrollVertically(1)) {
                        progress.visibility = View.VISIBLE
                        page++
                        moviesList.clear()
                        moviesViewModel.getMovies(genreId, page)
                    }
                }
            })
        }

    }

    private fun getViews(view: View) {
        moviesRecycler = view.findViewById(R.id.movies_recycler)
        progress = view.findViewById(R.id.movies_progress)
    }
}