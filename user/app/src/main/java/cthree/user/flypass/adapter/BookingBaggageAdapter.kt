package cthree.user.flypass.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cthree.user.flypass.databinding.BookingBaggageItemBinding

class BookingBaggageAdapter: RecyclerView.Adapter<BookingBaggageAdapter.ViewHolder>() {
    class ViewHolder(val binding: BookingBaggageItemBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = BookingBaggageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvTraveler.text = "Adult ${position + 1}"
    }

    override fun getItemCount(): Int {
        return 2
    }
}