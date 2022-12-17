package cthree.user.flypass.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cthree.user.flypass.databinding.HistoryBookingItemBinding
import cthree.user.flypass.models.booking.bookings.Booking
import cthree.user.flypass.utils.Utils

class BookingAdapter: RecyclerView.Adapter<BookingAdapter.ViewHolder>() {
    private lateinit var listener: OnItemClickListener
    interface OnItemClickListener{
        fun onItemClick(booking: Booking)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Booking>(){
        override fun areItemsTheSame(oldItem: Booking,newItem: Booking): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Booking,newItem: Booking): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    inner class ViewHolder(val binding: HistoryBookingItemBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                listener.onItemClick(differ.currentList[absoluteAdapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = HistoryBookingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val booking = differ.currentList[position]
        holder.binding.apply {
            tvBookingCode.text = booking.bookingCode
            tvDuration.text = Utils.formattedTime(booking.depFlight.duration)
            tvArriveTime.text = Utils.formattedTime(booking.depFlight.arrivalTime)
            tvDepartTime.text = Utils.formattedTime(booking.depFlight.departureTime)
            tvTicketPrice.text = Utils.formattedMoney(booking.totalPrice)
            tvSeatClass.text = "Economy"
            tvAirplaneName.text = "Garuda Indonesia"

            // TODO image airplane ga ada, nama airplane ga ada, seat class ga ada
//            Glide.with(root)
//                .load(booking.)
        }
    }

    fun setStatus(){

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Booking>) = differ.submitList(list)
}