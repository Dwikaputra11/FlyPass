package cthree.admin.flypass.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import cthree.admin.flypass.R
import cthree.admin.flypass.databinding.ItemTicketBinding
import cthree.admin.flypass.models.ticketflight.Flight
import cthree.admin.flypass.models.user.User

class TicketAdapter: RecyclerView.Adapter<TicketAdapter.ViewHolder>() {

    private lateinit var listener: OnItemClickListener
    interface OnItemClickListener{
        fun onItemClick(view: View, flight: Flight)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

    inner class ViewHolder(val binding: ItemTicketBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.btnDetail.setOnClickListener {
                listener.onItemClick(binding.btnDetail,differ.currentList[absoluteAdapterPosition])
            }
        }
    }

    private var diffCallback = object : DiffUtil.ItemCallback<Flight>() {
        override fun areItemsTheSame(oldItem: Flight, newItem: Flight): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Flight, newItem: Flight): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketAdapter.ViewHolder {
        var view = ItemTicketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: TicketAdapter.ViewHolder, position: Int) {
        val ticket = differ.currentList[position]

        holder.binding.tvDepartTime.text = ticket.departureTime
        holder.binding.tvArriveTime.text = ticket.arrivalTime
        holder.binding.iataDepartAirport.text = ticket.departureAirport.iata
        holder.binding.iataArriveAirport.text = ticket.arrivalAirport.iata
        holder.binding.tvDuration.text = ticket.duration
        holder.binding.tvDepartTime.text = ticket.departureTime
        holder.binding.tvSeatClass.text = ticket.flightClass.name
        holder.binding.tvTicketPrice.text = ticket.price.toString()
        holder.binding.tvDepartTime.text = ticket.departureTime
        holder.binding.tvAirplaneName.text = ticket.airline.name
        Glide.with(holder.itemView).load(ticket.airline.image).into(holder.binding.ivAirplaneLogo)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(ticketList: List<Flight>) = differ.submitList(ticketList)
}