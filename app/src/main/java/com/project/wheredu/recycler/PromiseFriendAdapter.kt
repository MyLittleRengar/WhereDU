package com.project.wheredu.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.wheredu.R

class PromiseFriendAdapter(val context: Context) :
    RecyclerView.Adapter<PromiseFriendAdapter.ViewHolder>() {

    interface CheckBoxStateListener {
        fun onCheckBoxStateChanged(position: Int, isChecked: Boolean, nickname: String)
    }

        var datas = mutableListOf<FriendItem>()
        var listener: CheckBoxStateListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.friend_add_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    override fun getItemCount(): Int = datas.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var userProfileImg: ImageView = itemView.findViewById(R.id.friendsAddProfileIV)
        private var userNickname: TextView = itemView.findViewById(R.id.friendsAddNameTV)
        private var userCheckBox: CheckBox = itemView.findViewById(R.id.friendsAddCB)

        fun bind(item: FriendItem) {
            userNickname.text = item.nickname
            Glide.with(itemView).load(item.profile).into(userProfileImg)
            userCheckBox.setOnCheckedChangeListener  {_, isChecked ->
                listener?.onCheckBoxStateChanged(adapterPosition, isChecked, item.nickname)
            }
        }
    }
}