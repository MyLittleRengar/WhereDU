package com.project.wheredu.recycler

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.wheredu.R
import com.project.wheredu.board.NoticeInfoActivity

class NoticeListAdapter(val context: Context) : RecyclerView.Adapter<NoticeListAdapter.ViewHolder>() {

    var datas = mutableListOf<NoticeItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.notice_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    override fun getItemCount(): Int = datas.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var noticeName: TextView = itemView.findViewById(R.id.noticeNameTV)
        private var noticeDate: TextView = itemView.findViewById(R.id.noticeDateTV)

        fun bind(item: NoticeItem) {
            noticeName.text = item.name
            noticeDate.text = item.date


            itemView.setOnClickListener {
                val intent = Intent(context, NoticeInfoActivity::class.java)
                intent.putExtra("noticeName", item.name)
                context.startActivity(intent)
            }
        }
    }
}