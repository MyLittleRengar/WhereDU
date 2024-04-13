package com.project.wheredu.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.wheredu.R

class MainPlaceAdapter(private val itemList: ArrayList<MainPlaceItem>): RecyclerView.Adapter<MainPlaceAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.nearstore_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.image.setImageResource(R.drawable.free)
        holder.name.text = itemList[position].name
        holder.tel.text = itemList[position].tel
        holder.distance.text = itemList[position].distance
        holder.info.setOnClickListener {
            itemClickListener.onClick(itemList[position].infoUrl)
        }
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.rvItem_nearstoreIv)
        val name: TextView = itemView.findViewById(R.id.rvItem_storeNameTv)
        val tel: TextView = itemView.findViewById(R.id.rvItem_near_storeTelTv)
        val distance: TextView = itemView.findViewById(R.id.rvItem_near_storeDistanceTv)
        val info: TextView = itemView.findViewById(R.id.rvItem_storeInfoTv)
    }

    interface OnItemClickListener {
        fun onClick(url: String)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener : OnItemClickListener
}