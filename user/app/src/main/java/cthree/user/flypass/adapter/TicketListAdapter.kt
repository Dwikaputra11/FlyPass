package cthree.user.flypass.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import cthree.user.flypass.databinding.TicketListItemBinding
import cthree.user.flypass.models.flight.Flight
import cthree.user.flypass.utils.Constants
import cthree.user.flypass.utils.Utils

class TicketListAdapter(): RecyclerView.Adapter<TicketListAdapter.ViewHolder>() {
    private lateinit var listener: OnItemClickListener

    interface OnItemClickListener{
        fun onItemClick(view: View, flight: Flight)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }
    inner class ViewHolder(val binding: TicketListItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(ticket: Flight){
            val timeDepart = Utils.formattedTime(ticket.departureTime)
            val timeArrive = Utils.formattedTime(ticket.arrivalTime)
            binding.iataArriveAirport.text = ticket.arrivalAirport.iata
            binding.iataDepartAirport.text = ticket.departureAirport.iata
            binding.tvAirplaneName.text = ticket.airline.name
            binding.tvArriveTime.text = timeArrive
            binding.tvDepartTime.text = timeDepart
            binding.tvDuration.text = ticket.duration
            binding.tvSeatClass.text = ticket.flightClass.name
            binding.tvTicketPrice.text = Utils.formattedMoney(ticket.price)
            Glide.with(binding.root)
                .load(ticket.airline.image)
                .into(binding.ivAirplaneLogo)
        }


        init {
            binding.btnDetail.setOnClickListener {
                listener.onItemClick(binding.btnDetail,differ.currentList[absoluteAdapterPosition])
            }
            binding.container.setOnClickListener {
                listener.onItemClick(binding.container,differ.currentList[absoluteAdapterPosition])
            }
        }
    }

    private var diffCallback = object : DiffUtil.ItemCallback<Flight>() {
        override fun areItemsTheSame(oldItem: Flight, newItem: Flight): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Flight, newItem: Flight): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = TicketListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(ticketList: List<Flight>) = differ.submitList(ticketList)

    fun cleanList() = differ.currentList.clear()
}