package cthree.admin.flypass.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import cthree.admin.flypass.databinding.ConfirmBookingItemBinding
import cthree.admin.flypass.models.transaction.Transaction
import cthree.admin.flypass.utils.Utils.convertISOTime

class ConfirmBookingAdapter: RecyclerView.Adapter<ConfirmBookingAdapter.ViewHolder>() {

    private lateinit var listener: OnItemClickListener

    interface OnItemClickListener{
        fun onItemClick(transaction: Transaction)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Transaction>(){
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(this, diffCallback)

    inner class ViewHolder(private val binding: ConfirmBookingItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Transaction){
            Glide.with(binding.root)
                .load(item.image)
                .into(binding.ivImage)
            binding.txtIdBooking.text = item.bookingId.toString()
            binding.tvDatePayed.text = convertISOTime(binding.root.context, item.datePayed)
            binding.root.setOnClickListener {
                listener.onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ConfirmBookingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Transaction>) = differ.submitList(list)
}