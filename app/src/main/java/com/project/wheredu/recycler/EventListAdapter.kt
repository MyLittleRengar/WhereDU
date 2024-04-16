package com.project.wheredu.recycler

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.wheredu.EventInfoActivity
import com.project.wheredu.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class EventListAdapter(val context: Context) : RecyclerView.Adapter<EventListAdapter.ViewHolder>() {

    var datas = mutableListOf<EventItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.event_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    override fun getItemCount(): Int = datas.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var eventName: TextView = itemView.findViewById(R.id.eventNameTV)
        private var eventDay: TextView = itemView.findViewById(R.id.eventD_DayTV)
        private var eventStartDate: TextView = itemView.findViewById(R.id.eventStartDateTV)
        private var eventEndDate: TextView = itemView.findViewById(R.id.eventEndDateTV)

        fun bind(item: EventItem) {
            eventName.text = item.name
            eventStartDate.text = item.start
            eventEndDate.text = item.end
            val today = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
            val endString = LocalDate.parse(item.end, formatter)
            val daysUntilEnd = ChronoUnit.DAYS.between(today, endString)
            val dDay =
                if (daysUntilEnd > 0) {
                    "D-${daysUntilEnd}"
                } else if (daysUntilEnd == 0L) {
                    "D-Day"
                } else {
                    "종료"
                }
            eventDay.text = dDay


            itemView.setOnClickListener {
                val intent = Intent(context, EventInfoActivity::class.java)
                intent.putExtra("eventName", item.name)
                intent.putExtra("eventDay", dDay)
                intent.putExtra("eventStart", item.start)
                intent.putExtra("eventEnd", item.end)
                context.startActivity(intent)
            }
        }
    }
}