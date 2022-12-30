package cthree.user.flypass.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cthree.user.flypass.databinding.WishlistItemBinding
import cthree.user.flypass.models.wishlist.get.WishList
import cthree.user.flypass.models.wishlist.get.WishListItem
import cthree.user.flypass.utils.Utils

private const val TAG = "WishlistAdapter"
class WishlistAdapter: RecyclerView.Adapter<WishlistAdapter.ViewHolder>() {

    private lateinit var listener: OnItemClickListener

    interface OnItemClickListener{
        fun onClick(wishlist: WishListItem)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

    private val diffCallback = object : DiffUtil.ItemCallback<WishListItem>(){
        override fun areItemsTheSame(oldItem: WishListItem, newItem: WishListItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: WishListItem, newItem: WishListItem): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(this, diffCallback)


    inner class ViewHolder(val binding: WishlistItemBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(wishlist: WishListItem){
            binding.tvKeberangkatan.text = wishlist.departureAirport.city
            binding.tvTujuan.text = wishlist.arrivalAirport.city
            binding.tvTanggal.text = Utils.convertDateToDayMonYear(wishlist.departureDate)
            binding.tvSeatClass.text = wishlist.flightClass.name
            binding.tvHarga.text = "Rp ${Utils.formattedMoney(wishlist.price)}"
            binding.tvDuration.text = Utils.formattedDuration(wishlist.duration)
            binding.tvAirplaneName.text = wishlist.airline.name
            binding.tvSeatClass.text = wishlist.flightType.name
            binding.tvIataArrive.text = wishlist.departureAirport.iata
            binding.tvIataDepart.text = wishlist.arrivalAirport.iata
            binding.tvDepartTime.text = Utils.formattedTime(wishlist.departureTime)
            binding.tvArriveTime.text = Utils.formattedTime(wishlist.arrivalTime)
            binding.cbShowMore.setOnClickListener {
                binding.rlDetail.isVisible = binding.cbShowMore.isChecked
            }
        }

        init {
            binding.root.setOnClickListener {
                listener.onClick(differ.currentList[absoluteAdapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = WishlistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun getList(): List<WishListItem> = differ.currentList

    fun submitList(list: List<WishListItem>?){
        differ.submitList(list)
    }
}