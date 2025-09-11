package com.erikh.mobilt_java24_erik_hultqvist_api_intergration_v5

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LocationAdapter(
    private val locations: MutableList<LocationItem>,
    private val onDeleteClicked: (LocationItem) -> Unit,
    private val onLocationClicked: (LocationItem) -> Unit
) : RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    inner class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvLocationName: TextView = itemView.findViewById(R.id.tvLocationName)
        val tvCoordinates: TextView = itemView.findViewById(R.id.tvCoordinates)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)

        val locationCard: View = itemView.findViewById(R.id.locationCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_location, parent, false)
        return LocationViewHolder(view)
    }

    override fun getItemCount(): Int = locations.size

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = locations[position]
        holder.tvLocationName.text = location.locationName
        holder.tvCoordinates.text = "Lat: ${location.latitude}, Lon: ${location.longitude}"

        holder.btnDelete.setOnClickListener {
            onDeleteClicked(location)
        }

        holder.locationCard.setOnClickListener {
            onLocationClicked(location)
        }
    }

    fun removeAt(position: Int) {
        locations.removeAt(position)
        notifyItemRemoved(position)
    }

    fun updateList(newLocations: List<LocationItem>) {
        locations.clear()
        locations.addAll(newLocations)
        notifyDataSetChanged()
    }
}
