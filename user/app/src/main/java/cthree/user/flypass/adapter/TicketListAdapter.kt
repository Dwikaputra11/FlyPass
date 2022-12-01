package cthree.user.flypass.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cthree.user.flypass.data.Ticket
import cthree.user.flypass.databinding.TicketListItemBinding

class TicketListAdapter(): RecyclerView.Adapter<TicketListAdapter.ViewHolder>() {
    private lateinit var listener: OnItemClickListener
    interface OnItemClickListener{
        fun onItemClick(view: View, ticket: Ticket)
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

    private var diffCallback = object : DiffUtil.ItemCallback<Ticket>() {
        override fun areItemsTheSame(oldItem: Ticket, newItem: Ticket): Boolean {
            return oldItem.seatClass == newItem.seatClass
        }

        override fun areContentsTheSame(oldItem: Ticket, newItem: Ticket): Boolean {
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
        holder.binding.aitaArriveAirport.text = ticket.aitaArrival
        holder.binding.aitaDepartAirport.text = ticket.aitaDeparture
        holder.binding.tvAirplaneName.text = ticket.airplaneName
        holder.binding.tvArriveTime.text = ticket.arrivalTime
        holder.binding.tvDepartTime.text = ticket.departureTime
        holder.binding.tvDuration.text = ticket.duration.toString()
        holder.binding.tvSeatClass.text = ticket.seatClass
        holder.binding.tvTicketPrice.text = ticket.price.toString()
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(ticketList: List<Ticket>) = differ.submitList(ticketList)

    fun cleanList() = differ.currentList.clear()
}