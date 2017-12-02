package it.personal.claudiopaccone.starsfinder.search.list

import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import it.personal.claudiopaccone.starsfinder.R
import it.personal.claudiopaccone.starsfinder.api.models.Stargazer
import kotlinx.android.synthetic.main.stargazer_item.view.*

class StargazerVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(stargazer: Stargazer) {
        itemView.usernameTextView.text = stargazer.username
        Glide.with(itemView.context)
                .load(Uri.parse(stargazer.avatarUrl))
                .into(itemView.avatarImageView)

    }
}

class StargazerAdapter(private var stargazersList: List<Stargazer>) : RecyclerView.Adapter<StargazerVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StargazerVH {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.stargazer_item, parent, false)

        return StargazerVH(itemView)
    }

    override fun onBindViewHolder(holder: StargazerVH, position: Int) {
        holder.bind(stargazersList[position])
    }

    override fun getItemCount() = stargazersList.size

    fun addItems(newStargazer: List<Stargazer>) {
        stargazersList = stargazersList.plus(newStargazer)
        notifyDataSetChanged()
    }

    fun deleteAllItems() {
        stargazersList = emptyList()
        notifyDataSetChanged()
    }

}