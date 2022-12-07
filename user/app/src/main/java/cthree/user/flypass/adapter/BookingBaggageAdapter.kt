package cthree.user.flypass.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cthree.user.flypass.data.Baggage
import cthree.user.flypass.databinding.BookingBaggageItemBinding

private const val TAG = "BookingBaggageAdapter"
class BookingBaggageAdapter(private val passengerAmount: Int): RecyclerView.Adapter<BookingBaggageAdapter.ViewHolder>() {

    private lateinit var baggageList: List<String?>

    private val diffCallback = object : DiffUtil.ItemCallback<Baggage?>(){
        override fun areItemsTheSame(oldItem: Baggage, newItem: Baggage): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Baggage, newItem: Baggage): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    class ViewHolder(val binding: BookingBaggageItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = BookingBaggageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvTraveler.text = "Adult ${position + 1}"
        if(differ.currentList.isNotEmpty() && differ.currentList.size != holder.absoluteAdapterPosition){
            holder.binding.baggageWeight.text = differ.currentList[position]?.weight.toString()
        }
    }

    override fun getItemCount(): Int {
        return passengerAmount
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<Baggage?>){
        Log.d(TAG, "submitList: $list")
        differ.submitList(null)
        notifyDataSetChanged()
        differ.submitList(list)
        notifyItemRangeInserted(0, list.size)
    }
}