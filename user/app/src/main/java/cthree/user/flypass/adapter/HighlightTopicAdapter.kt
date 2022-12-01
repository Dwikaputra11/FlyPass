package cthree.user.flypass.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import cthree.user.flypass.data.HighlightTopic
import cthree.user.flypass.databinding.HighligtTopicBinding

class HighlightTopicAdapter(private val list: List<HighlightTopic>): RecyclerView.Adapter<HighlightTopicAdapter.ViewHolder>() {

    class ViewHolder(val binding: HighligtTopicBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = HighligtTopicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.root.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvTitle.text = list[position].title
    }

    override fun getItemCount(): Int {
        return list.size
    }
}