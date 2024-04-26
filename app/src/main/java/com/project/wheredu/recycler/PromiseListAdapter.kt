package com.project.wheredu.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.wheredu.R

class PromiseListAdapter(private val itemList: ArrayList<PromiseItem>): RecyclerView.Adapter<PromiseListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.promise_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = itemList[position].name
        holder.place.text = itemList[position].place
        holder.time.text = itemList[position].time
        holder.date.text = itemList[position].date
        holder.promiseInfo.setOnClickListener {
            itemClickListener.onInfoClick(position, itemList[position].name, itemList[position].time)
        }
        holder.promiseDelete.setOnClickListener {
            itemClickListener.onDeleteClick(position, itemList[position].name, itemList[position].time)
        }
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.promise_PromiseNameTV)
        val place: TextView = itemView.findViewById(R.id.promise_PromisePlaceTV)
        val time: TextView = itemView.findViewById(R.id.promise_PromiseTimeTV)
        val date: TextView = itemView.findViewById(R.id.promise_PromiseDateTV)
        val promiseInfo: ImageView = itemView.findViewById(R.id.promise_PromiseInfoIV)
        val promiseDelete: ImageView = itemView.findViewById(R.id.promise_PromiseDeleteIV)
    }

    interface OnItemClickListener {
        fun onInfoClick(position: Int, name: String, date: String)
        fun onDeleteClick(position: Int, name: String, date: String)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener : OnItemClickListener
}