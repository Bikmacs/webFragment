package com.bignerdranch.android.webFragment.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.application_practica2.R
import com.bignerdranch.android.webFragment.adapters.MovieAdapter.*
import com.bignerdranch.android.webFragment.ui.models.Movie


class MovieAdapter(private var movies: List<Movie>, private val context: Context) : RecyclerView.Adapter<MovieAdapter.ViewHolder>() {

    private var items: List<Movie> = movies

    interface onItemClickMovie {
        fun onMovieClick(movieUrl: String)
    }

    private var movieClickListener: onItemClickMovie? = null

    fun setOnMovieClickListener(listener: onItemClickMovie) {
        this.movieClickListener = listener
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val movieName: TextView = view.findViewById(R.id.movieName)
        val movieRating: TextView = view.findViewById(R.id.movieRating)
        val movieLayout: LinearLayout = view.findViewById(R.id.linear)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.movie_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items.sortedByDescending { it.rating }[position]
        holder.movieName.text = item.movie
        holder.movieRating.text = item.rating.toString()

        holder.movieLayout.setOnClickListener {
            movieClickListener?.onMovieClick(item.imdb_url)
        }



    }

    fun updateMovie(items: List<Movie>) {
        this.items = items
        notifyDataSetChanged()
    }
}

