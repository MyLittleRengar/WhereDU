package com.project.wheredu.recycler

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.wheredu.R

class WeatherAdapter (private var items : Array<ModelWeather>) : RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_weather, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.setItem(item)
    }

    override fun getItemCount() = items.count()

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun setItem(item : ModelWeather) {
            val imgWeather = itemView.findViewById<ImageView>(R.id.imgWeather)
            val tvTime = itemView.findViewById<TextView>(R.id.tvTime)
            val tvHumidity = itemView.findViewById<TextView>(R.id.tvHumidity)
            val tvTemp = itemView.findViewById<TextView>(R.id.tvTemp)

            imgWeather.setImageResource(getRainImage(item.rainType, item.sky))
            tvTime.text = getTime(item.fcstTime)
            tvHumidity.text = item.humidity +"%"
            tvTemp.text = item.temp + "°"
        }
    }

    fun getTime(factTime : String): String {
        if(factTime != "지금"){
            var hourSystem : Int = factTime.toInt()
            val hourSystemString: String

            if(hourSystem == 0){
                return "오전 12시"
            }else if(hourSystem > 2100){
                hourSystem -= 1200
                hourSystemString = hourSystem.toString()
                return "오후 ${hourSystemString[0]}${hourSystemString[1]}시"
            }else if(hourSystem == 1200){
                return "오후 12시"
            } else if(hourSystem > 1200){
                hourSystem -= 1200
                hourSystemString = hourSystem.toString()
                return "오후 ${hourSystemString[0]}시"
            }
            else if(hourSystem >= 1000){
                hourSystemString = hourSystem.toString()
                return "오전 ${hourSystemString[0]}${hourSystemString[1]}시"
            }else{
                hourSystemString = hourSystem.toString()
                return "오전 ${hourSystemString[0]}시"
            }
        }else{
            return factTime
        }
    }
    fun getRainImage(rainType : String, sky: String) : Int {
        return when(rainType) {
            "0" -> getWeatherImage(sky)
            "1" -> R.drawable.rainy
            "2" -> R.drawable.hail
            "3" -> R.drawable.snowy
            "4" -> R.drawable.brash
            else -> getWeatherImage(sky)
        }
    }

    private fun getWeatherImage(sky : String) : Int {
        return when(sky) {
            "1" -> R.drawable.sun
            "3" ->  R.drawable.cloudy
            "4" -> R.drawable.blur
            else -> R.drawable.ic_launcher_foreground
        }
    }
}