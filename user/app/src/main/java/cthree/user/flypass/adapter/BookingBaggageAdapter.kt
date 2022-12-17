package cthree.user.flypass.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cthree.user.flypass.data.PassengerBaggage
import cthree.user.flypass.databinding.BookingBaggageItemBinding

private const val TAG = "BookingBaggageAdapter"
class BookingBaggageAdapter(private val passengerAmount: Int): RecyclerView.Adapter<BookingBaggageAdapter.ViewHolder>() {

    private val baggageList: MutableList<PassengerBaggage> = mutableListOf()

    private val diffCallback = object : DiffUtil.ItemCallback<PassengerBaggage>(){
        override fun areItemsTheSame(oldItem: PassengerBaggage, newItem: PassengerBaggage): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: PassengerBaggage, newItem: PassengerBaggage): Boolean {
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
            holder.binding.baggageWeight.text = differ.currentList[position].baggageList.first()
        }
        baggageList.add(PassengerBaggage(mutableListOf(holder.binding.baggageWeight.toString())))
    }

    fun getBaggageDefaultVal(): List<PassengerBaggage> = baggageList

    override fun getItemCount(): Int {
        return passengerAmount
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<PassengerBaggage>){
        Log.d(TAG, "submitList: $list")
        differ.submitList(null)
        notifyDataSetChanged()
        differ.submitList(list)
        notifyItemRangeInserted(0, list.size)
    }
}