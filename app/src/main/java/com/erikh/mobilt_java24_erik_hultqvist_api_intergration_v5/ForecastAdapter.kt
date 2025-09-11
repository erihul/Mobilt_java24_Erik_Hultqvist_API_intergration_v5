package com.erikh.mobilt_java24_erik_hultqvist_api_intergration_v5

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*

class ForecastAdapter(private val forecastList: List<WeatherService.ForecastItem>) :
    RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {

    private var lastShownDay = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.forecast_item, parent, false)
        return ForecastViewHolder(view)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val item = forecastList[position]

        // Format date and time
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = inputFormat.parse(item.dateTime)

        val dayFormat = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val dayText = if (date != null) dayFormat.format(date) else ""
        val timeText = if (date != null) timeFormat.format(date) else ""

        // Only show the day once
        if (dayText != lastShownDay) {
            holder.dateText.text = dayText
            holder.dateText.visibility = View.VISIBLE
            lastShownDay = dayText
        } else {
            holder.dateText.visibility = View.GONE
        }

        holder.timeText.text = timeText
        holder.tempText.text = "${item.temp}°C"
        holder.descriptionText.text = item.description.capitalize(Locale.ROOT)

        Glide.with(holder.itemView.context)
            .load(item.iconUrl)
            .into(holder.iconImage)
    }

    override fun getItemCount(): Int = forecastList.size

    class ForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateText: TextView = itemView.findViewById(R.id.dateText)
        val timeText: TextView = itemView.findViewById(R.id.timeText)
        val tempText: TextView = itemView.findViewById(R.id.tempText)
        val descriptionText: TextView = itemView.findViewById(R.id.descriptionText)
        val iconImage: ImageView = itemView.findViewById(R.id.forecastIcon)
    }
}