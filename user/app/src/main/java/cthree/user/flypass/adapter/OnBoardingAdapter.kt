package cthree.user.flypass.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import cthree.user.flypass.R
import cthree.user.flypass.data.OnBoarding
import cthree.user.flypass.databinding.OnBoardingItemBinding

class OnBoardingAdapter(private val list: List<OnBoarding>): RecyclerView.Adapter<OnBoardingAdapter.ViewHolder>() {

    class ViewHolder(val binding: OnBoardingItemBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = OnBoardingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.btnGetStarted.isVisible = holder.absoluteAdapterPosition == list.size - 1
        holder.binding.btnGetStarted.setOnClickListener {
            Navigation.findNavController(holder.binding.root).navigate(R.id.action_onBoardingFragment_to_joinMemberFragment)
        }

        Glide.with(holder.binding.root)
            .load(list[position].image)
            .into(holder.binding.ivOnBoard)
        holder.binding.tvTitle.text = holder.binding.root.resources.getString(list[position].title)
        holder.binding.tvMsg.text = list[position].msg
    }

    override fun getItemCount(): Int {
        return list.size
    }
}