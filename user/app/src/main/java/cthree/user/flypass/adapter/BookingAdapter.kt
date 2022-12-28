package cthree.user.flypass.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import cthree.user.flypass.R
import cthree.user.flypass.databinding.HistoryBookingItemBinding
import cthree.user.flypass.models.booking.bookings.Booking
import cthree.user.flypass.utils.BookingStatus
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
        fun bind(booking: Booking){
            binding.apply {
                tvBookingCode.text = booking.bookingCode
                tvDuration.text = Utils.formattedTime(booking.depFlight.duration)
                tvArriveTime.text = Utils.formattedTime(booking.depFlight.arrivalTime)
                tvDepartTime.text = Utils.formattedTime(booking.depFlight.departureTime)
                tvTicketPrice.text = Utils.formattedMoney(booking.totalPrice)
                tvSeatClass.text = "Economy"
                tvAirplaneName.text = booking.depFlight.airline.name
                tvBookingStatus.text = booking.bookingStatus.name
                iataDepartAirport.text = booking.depFlight.departureAirport.iata
                iataArriveAirport.text = booking.depFlight.arrivalAirport.iata

                Glide.with(root)
                    .load(booking.depFlight.airline.image)
                    .into(ivAirplaneLogo)
                when (booking.bookingStatus.id) {
                    BookingStatus.PAID.ordinal -> {
                        cvBookingStatus.setCardBackgroundColor(ContextCompat.getColor(view.context,R.color.orange_500))
                    }
                    BookingStatus.WAITING.ordinal -> {
                        cvBookingStatus.setCardBackgroundColor(ContextCompat.getColor(view.context,R.color.color_primary))
                    }
                    BookingStatus.COMPLETE.ordinal -> {
                        cvBookingStatus.setCardBackgroundColor(ContextCompat.getColor(view.context,R.color.green_500))
                    }
                }
            }
        }

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
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Booking>) = differ.submitList(list)
}