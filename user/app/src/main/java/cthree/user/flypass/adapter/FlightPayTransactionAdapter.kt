package cthree.user.flypass.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cthree.user.flypass.databinding.RowLatestTransactionBinding
import cthree.user.flypass.models.flightpay.history.History
import cthree.user.flypass.models.flightpay.history.HistoryTopupList
import cthree.user.flypass.utils.Utils

class FlightPayTransactionAdapter:RecyclerView.Adapter<FlightPayTransactionAdapter.ViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<History>(){
        override fun areItemsTheSame(
            oldItem: History,
            newItem: History
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: History,
            newItem: History
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    private val differ = AsyncListDiffer(this, diffCallback)


    class ViewHolder(val binding: RowLatestTransactionBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(history: History){
            binding.tvType.text = if(history.type == "+") "Pembelian" else "Pembayaran"
            binding.tvDate.text = Utils.convertISOTime(binding.root.context, history.updatedAt)
            binding.tvNominal.text = "${history.type}Rp ${Utils.formattedMoney(history.balance)}"
            binding.tvStatus.text = history.status
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowLatestTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<History>) = differ.submitList(list)
}