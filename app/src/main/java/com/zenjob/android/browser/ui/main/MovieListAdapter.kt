package com.zenjob.android.browser.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zenjob.android.browser.R
import com.zenjob.android.browser.data.Movie
import com.zenjob.android.browser.data.POSTER_BASE_URL
import com.zenjob.android.browser.data.repository.NetworkState
import kotlinx.android.synthetic.main.network_state_item.view.*
import kotlinx.android.synthetic.main.viewholder_movie_item.view.*

class MovieListAdapter() : PagedListAdapter<Movie, RecyclerView.ViewHolder>(
    MovieDiffCallback()) {

    val MOVIE_VIEW_TYPE = 1
    val NETWORK_VIEW_TYPE = 2

    private var networkState: NetworkState? = null


    var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onMovieItemClick(
            itemView: View,
            position: Int,
            movie: Movie
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view: View

        if (viewType == MOVIE_VIEW_TYPE) {
            view = layoutInflater.inflate(R.layout.viewholder_movie_item, parent, false)
            return MovieViewHolder(view)
        } else {
            view = layoutInflater.inflate(R.layout.network_state_item, parent, false)
            return NetworkStateItemViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {Int
        val movie = getItem(position)
        //movie?.let { (holder as MovieViewHolder).bind(it, listener) }
        if (getItemViewType(position) == MOVIE_VIEW_TYPE) {
            movie?.let { (holder as MovieViewHolder).bind(it,listener) }
        }
        else {
            (holder as NetworkStateItemViewHolder).bind(networkState)
        }

    }

    private fun hasExtraRow(): Boolean {
        return networkState != null && networkState != NetworkState.LOADED
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            NETWORK_VIEW_TYPE
        } else {
            MOVIE_VIEW_TYPE
        }
    }
    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        val titleTv: TextView = itemView.findViewById(R.id.title)
        val ratingTv: TextView = itemView.findViewById(R.id.rating)
        val releaseDateTv: TextView = itemView.findViewById(R.id.release_date)

        fun bind(movie: Movie?, listener: OnItemClickListener?) {

            titleTv.text = movie?.title
            releaseDateTv.text = android.text.format.DateFormat.format("yyyy", movie?.releaseDate)
            ratingTv.text = "${movie?.voteAverage ?: 0}"

            val moviePosterURL = POSTER_BASE_URL + movie?.posterPath
            Glide.with(itemView.context)
                .load(moviePosterURL)
                .into(itemView.cv_iv_movie_poster);

            itemView.setOnClickListener {
                // Triggers click upwards to the adapter on click
                if (listener != null) {
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onMovieItemClick(itemView, position, movie!!)
                    }
                }
            }

        }
    }


    class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    }
    class NetworkStateItemViewHolder (view: View) : RecyclerView.ViewHolder(view) {

        fun bind(networkState: NetworkState?) {
            if (networkState != null && networkState == NetworkState.LOADING) {
                itemView.progress_bar_item.visibility = View.VISIBLE
            }
            else  {
                itemView.progress_bar_item.visibility = View.GONE
            }

            if (networkState != null && networkState == NetworkState.ERROR) {
                itemView.error_msg_item.visibility = View.VISIBLE
                itemView.error_msg_item.text = networkState.msg
            }
            else if (networkState != null && networkState == NetworkState.ENDOFLIST) {
                itemView.error_msg_item.visibility = View.VISIBLE
                itemView.error_msg_item.text = networkState.msg
            }
            else {
                itemView.error_msg_item.visibility = View.GONE
            }
        }
    }


    fun setNetworkState(newNetworkState: NetworkState) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()

        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {                             //hadExtraRow is true and hasExtraRow false
                notifyItemRemoved(super.getItemCount())    //remove the progressbar at the end
            } else {                                       //hasExtraRow is true and hadExtraRow false
                notifyItemInserted(super.getItemCount())   //add the progressbar at the end
            }
        } else if (hasExtraRow && previousState != newNetworkState) { //hasExtraRow is true and hadExtraRow true and (NetworkState.ERROR or NetworkState.ENDOFLIST)
            notifyItemChanged(itemCount - 1)       //add the network message at the end
        }

    }

}
