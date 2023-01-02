package cthree.admin.flypass.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import cthree.admin.flypass.R
import cthree.admin.flypass.databinding.ItemUserAccountBinding
import cthree.admin.flypass.models.user.User

class UserAccountAdapter(): RecyclerView.Adapter<UserAccountAdapter.ViewHolder>() {

    private lateinit var listener: OnItemClickListener
    interface OnItemClickListener{
        fun onItemClick(user: User)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

    inner class ViewHolder(val binding: ItemUserAccountBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                listener.onItemClick(differ.currentList[absoluteAdapterPosition])
            }
        }
    }

    private var diffCallback = object : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAccountAdapter.ViewHolder {
        var view = ItemUserAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserAccountAdapter.ViewHolder, position: Int) {
        val user = differ.currentList[position]

        holder.binding.txtName.text = user.name
        holder.binding.txtEmail.text = user.email
        holder.binding.txtTglLahir.text = user.birthDate
        Glide.with(holder.itemView).load(user.image).placeholder(R.drawable.ic_johndoe).into(holder.binding.imgUser)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(userList: List<User>) = differ.submitList(userList)
}