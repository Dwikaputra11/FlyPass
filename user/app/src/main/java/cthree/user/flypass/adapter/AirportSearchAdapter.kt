package cthree.user.flypass.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cthree.user.flypass.databinding.AirportSearchItemBinding
import cthree.user.flypass.models.airport.Airport

class AirportSearchAdapter(): RecyclerView.Adapter<AirportSearchAdapter.ViewHolder>() {

    private lateinit var listener: OnItemClickListener

    interface OnItemClickListener{
        fun onItemClick(airport: Airport)
    }

    fun senOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Airport>(){
        override fun areItemsTheSame(oldItem: Airport, newItem: Airport): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Airport, newItem: Airport): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    inner class ViewHolder(val binding: AirportSearchItemBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                listener.onItemClick(differ.currentList[absoluteAdapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AirportSearchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val airport = differ.currentList[position]
        holder.binding.tvAirportLocation.text = "${airport.city}, ${airport.country}"
        holder.binding.tvAirportName.text = "${airport.name} (${airport.iata})"
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Airport>) = differ.submitList(list)
}