package com.project.wheredu.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.wheredu.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class EventListAdapter(private val itemList: ArrayList<EventItem>): RecyclerView.Adapter<EventListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.event_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = itemList[position].name
        holder.start.text = itemList[position].start
        holder.end.text = itemList[position].end
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
        val endString = LocalDate.parse(itemList[position].end, formatter)
        val daysUntilEnd = ChronoUnit.DAYS.between(today, endString).toString()
        holder.dDay.text = daysUntilEnd
        holder.itemView.setOnClickListener {
            itemClickListener.onInfoClick(position, itemList[position].name, itemList[position].start, itemList[position].end, daysUntilEnd)
        }
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.eventNameTV)
        val start: TextView = itemView.findViewById(R.id.eventStartDateTV)
        val end: TextView = itemView.findViewById(R.id.eventEndDateTV)
        val dDay: TextView = itemView.findViewById(R.id.eventD_DayTV)
    }

    interface OnItemClickListener {
        fun onInfoClick(position: Int, name: String, start: String, end: String, day: String)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener : OnItemClickListener
}