package cthree.admin.flypass.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cthree.admin.flypass.databinding.AirlineSearchItemBinding
import cthree.admin.flypass.models.airline.Airline

class AirlineSearchAdapter : RecyclerView.Adapter<AirlineSearchAdapter.ViewHolder>() {

    private lateinit var listener: OnItemClickListener

    interface OnItemClickListener{
        fun onItemClick(airline: Airline)
    }

    fun senOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Airline>(){
        override fun areItemsTheSame(oldItem: Airline, newItem: Airline): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Airline, newItem: Airline): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    inner class ViewHolder(val binding: AirlineSearchItemBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                listener.onItemClick(differ.currentList[absoluteAdapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AirlineSearchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val airline = differ.currentList[position]
        holder.binding.tvAirlineIata.text = airline.iata
        holder.binding.tvAirlineName.text = airline.name
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Airline>) = differ.submitList(list)

}