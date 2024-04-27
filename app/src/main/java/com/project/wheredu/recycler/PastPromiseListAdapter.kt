package com.project.wheredu.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.wheredu.R

class PastPromiseListAdapter(private val itemList: ArrayList<PastPromiseItem>): RecyclerView.Adapter<PastPromiseListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.past_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = itemList[position].name
        holder.friend.text = itemList[position].friend
        holder.time.text = itemList[position].time
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.pastPromiseNameTV)
        val friend: TextView = itemView.findViewById(R.id.pastPromiseFriendsTV)
        val time: TextView = itemView.findViewById(R.id.pastPromiseTimeTV)
    }
}