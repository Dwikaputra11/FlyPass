package cthree.user.flypass.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cthree.user.flypass.databinding.AirportSearchItemBinding
import cthree.user.flypass.models.airport.Airport

class AirportSearchAdapter(private val list: List<Airport>): RecyclerView.Adapter<AirportSearchAdapter.ViewHolder>() {

    private lateinit var listener: OnItemClickListener

    interface OnItemClickListener{
        fun onItemClick(airport: Airport)
    }

    fun senOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

    inner class ViewHolder(val binding: AirportSearchItemBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                listener.onItemClick(list[absoluteAdapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AirportSearchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val airport = list[position]
        holder.binding.tvAirportLocation.text = "${airport.city}, ${airport.country}"
        holder.binding.tvAirportName.text = "${airport.name} (${airport.iata})"
    }

    override fun getItemCount(): Int {
        return list.size
    }
}