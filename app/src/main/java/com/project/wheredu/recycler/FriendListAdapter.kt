package com.project.wheredu.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.wheredu.R

class FriendListAdapter(val context: Context): RecyclerView.Adapter<FriendListAdapter.ViewHolder>() {

    private var datas = mutableListOf<FriendItem>()

    interface OnItemClickListener {
        fun onItemClick(v: View, data: FriendItem, pos: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.friends_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    override fun getItemCount(): Int = datas.size

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private var userProfileImg: ImageView = itemView.findViewById(R.id.friendsItemProfileIV)
        private var userNickname: TextView = itemView.findViewById(R.id.friendsItemNameTV)

        fun bind(item: FriendItem) {
            userNickname.text = item.nickname
            Glide.with(itemView).load(item.profile).into(userProfileImg)

            val pos = adapterPosition
            if(pos != RecyclerView.NO_POSITION) {
                itemView.setOnClickListener {
                    listener?.onItemClick(itemView, item, pos)
                }
            }
        }
    }
}