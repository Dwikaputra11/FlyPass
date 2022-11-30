package cthree.user.flypass.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cthree.user.flypass.data.Ticket
import cthree.user.flypass.databinding.TicketListItemBinding

class TicketListAdapter(private val ticketList: ArrayList<Ticket>): RecyclerView.Adapter<TicketListAdapter.ViewHolder>() {
    private lateinit var listener: OnItemClickListener
    interface OnItemClickListener{
        fun onItemClick(ticket: Ticket)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }
    inner class ViewHolder(val binding: TicketListItemBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.btnDetail.setOnClickListener {
                listener.onItemClick(ticketList[absoluteAdapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = TicketListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.aitaArriveAirport.text = ticketList[position].aitaArrival
        holder.binding.aitaDepartAirport.text = ticketList[position].aitaDeparture
//        holder.binding.tvAirplaneName.text = ticketList[position].airplaneName
        holder.binding.tvArriveTime.text = ticketList[position].arrivalTime
        holder.binding.tvDepartTime.text = ticketList[position].departureTime
        holder.binding.tvDuration.text = ticketList[position].duration.toString()
        holder.binding.tvSeatClass.text = ticketList[position].seatClass
        holder.binding.tvTicketPrice.text = ticketList[position].price.toString()
    }

    override fun getItemCount(): Int {
        return ticketList.size
    }
}