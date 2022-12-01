package cthree.user.flypass.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cthree.user.flypass.data.Traveler
import cthree.user.flypass.databinding.TravelerDetailsItemBinding
import kotlin.math.log

private const val TAG = "TravelerDetailsAdapter"
class TravelerDetailsAdapter(private val passengerAmount: Int): RecyclerView.Adapter<TravelerDetailsAdapter.ViewHolder>() {

    private lateinit var listener: OnItemClickListener


    interface OnItemClickListener{
        fun onItemClick(traveler: Traveler? ,position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Traveler?>(){
        override fun areItemsTheSame(oldItem: Traveler, newItem: Traveler): Boolean {
            return oldItem.idCard == newItem.idCard
        }

        override fun areContentsTheSame(oldItem: Traveler, newItem: Traveler): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    private val differ = AsyncListDiffer(this, diffCallback)

    inner class ViewHolder(val binding: TravelerDetailsItemBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if(differ.currentList.isNotEmpty() && differ.currentList.size != absoluteAdapterPosition){
                    listener.onItemClick(differ.currentList[absoluteAdapterPosition], absoluteAdapterPosition)
                }else{
                    listener.onItemClick(null, absoluteAdapterPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = TravelerDetailsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(differ.currentList.isNotEmpty() && differ.currentList.size != holder.absoluteAdapterPosition){
            Log.d(TAG, "onBindViewHolder: not empty")
            val traveler = differ.currentList[position]
            holder.binding.tvAdultName.text = if(traveler != null) "${traveler.title}. ${traveler.surname}, ${traveler.name}"
            else "Adult ${position + 1}"
        }else{
            holder.binding.tvAdultName.text = "Adult ${position + 1}"
        }
    }

    override fun getItemCount(): Int {
        return passengerAmount
    }

    fun submitList(list: List<Traveler?>) = differ.submitList(list)

    fun modifyItemList(list: List<Traveler?>, position: Int){
        differ.submitList(list)
        Log.d(TAG, "submitList: ${differ.currentList.size}")
        notifyItemChanged(position)
    }
}