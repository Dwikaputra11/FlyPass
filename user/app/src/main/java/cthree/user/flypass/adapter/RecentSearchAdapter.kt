package cthree.user.flypass.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cthree.user.flypass.R
import cthree.user.flypass.data.RecentSearch
import cthree.user.flypass.databinding.RowRecentBinding
import cthree.user.flypass.utils.Utils

class RecentSearchAdapter: RecyclerView.Adapter<RecentSearchAdapter.ViewHolder>(){

    private lateinit var listener: SetOnItemClickListener

    interface SetOnItemClickListener{
        fun onItemClick(search: RecentSearch)
    }

    fun setOnItemClickListener(listener: SetOnItemClickListener){
        this.listener = listener
    }

    private val diffCallback = object: DiffUtil.ItemCallback<RecentSearch>(){
        override fun areItemsTheSame(oldItem: RecentSearch, newItem: RecentSearch): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RecentSearch, newItem: RecentSearch): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    inner class ViewHolder(val binding: RowRecentBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                listener.onItemClick(differ.currentList[absoluteAdapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowRecentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val search = differ.currentList[position]
        val context = holder.binding.root.context
        with(holder.binding){
            tvArrivalDate.isVisible = search.arriveDate != null
            ivDateStatus.isVisible = search.arriveDate != null
            if(search.arriveDate == null) ivTripStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_arrow_right_alt_15))
            tvArriveCity.text = search.arriveCity
            tvDepartCity.text = search.departCity
            tvPassenger.text = search.passengerAmount.toString()
            tvSeatClass.text = search.seatClass
            tvDepartDate.text= Utils.convertDateSearch(search.departDate)
            if(search.arriveDate != null) tvArrivalDate.text = Utils.convertDateSearch(search.arriveDate)
        }
    }

    fun submitList(list: List<RecentSearch>) = differ.submitList(list)

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}