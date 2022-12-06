package cthree.user.flypass.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
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
        val ticket = differ.currentList[position]
//        val isoTimeDepart = "${ticket.departureDate}T${ticket.departureTime}Z"
//        val isoTimeArrive = "${ticket.arrivalDate}T${ticket.arrivalTime}Z"
        val timeDepart = Utils.formattedTime(ticket.departureTime)
//        val dateDepart = Utils.convertISOTime(holder.binding.root.context, isoTimeDepart, Constants.DATE_TYPE)
        val timeArrive = Utils.formattedTime(ticket.arrivalTime)
//        val dateArrive = Utils.convertISOTime(holder.binding.root.context, isoTimeArrive, Constants.DATE_TYPE)
        holder.binding.iataArriveAirport.text = ticket.arrivalAirport.iata
        holder.binding.iataDepartAirport.text = ticket.departureAirport.iata
        holder.binding.tvAirplaneName.text = ticket.airline.name
        holder.binding.tvArriveTime.text = timeArrive
        holder.binding.tvDepartTime.text = timeDepart
        holder.binding.tvDuration.text = ticket.duration
        holder.binding.tvSeatClass.text = ticket.flightClass.name
        holder.binding.tvTicketPrice.text = ticket.price.toString()
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(ticketList: List<Flight>) = differ.submitList(ticketList)

    fun cleanList() = differ.currentList.clear()
}