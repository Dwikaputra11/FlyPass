package cthree.user.flypass.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cthree.user.flypass.databinding.RowNotificationBinding
import cthree.user.flypass.models.airport.Airport
import cthree.user.flypass.models.notification.Notification

class NotificationAdapter: RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    private lateinit var listener: OnItemClickListener

    interface OnItemClickListener{
        fun onItemClick(notify: Notification)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Notification>(){
        override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(this, diffCallback)


    inner class ViewHolder(val binding: RowNotificationBinding):RecyclerView.ViewHolder(binding.root)  {
        fun bind(notify: Notification){
            binding.tvTitle.text = notify.bookingCode
            binding.tvDesc.text = notify.message
            binding.ivNext.isVisible = !notify.isRead
            binding.root.setOnClickListener {
                listener.onItemClick(notify)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Notification>) = differ.submitList(list)
}