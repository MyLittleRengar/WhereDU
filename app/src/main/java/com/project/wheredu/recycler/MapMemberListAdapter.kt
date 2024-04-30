package com.project.wheredu.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.wheredu.R

class MapMemberListAdapter(private val itemList: ArrayList<MapMemberItem>): RecyclerView.Adapter<MapMemberListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.map_member_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = itemList[position].nickname
        holder.userIcon.setImageResource(itemList[position].img)
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.mapMemberNicknameTV)
        val userIcon: ImageView = itemView.findViewById(R.id.mapMemberIconIV)
    }
}