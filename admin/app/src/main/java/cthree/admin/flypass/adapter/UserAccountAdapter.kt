package cthree.admin.flypass.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import cthree.admin.flypass.databinding.ItemUserAccountBinding
import cthree.admin.flypass.models.user.User

class UserAccountAdapter(private var listUserAccount : List<User>): RecyclerView.Adapter<UserAccountAdapter.ViewHolder>() {

    class ViewHolder(var binding: ItemUserAccountBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAccountAdapter.ViewHolder {
        var view = ItemUserAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserAccountAdapter.ViewHolder, position: Int) {
        holder.binding.txtName.text = listUserAccount[position].name
        holder.binding.txtEmail.text = listUserAccount[position].email
        holder.binding.txtTglLahir.text = listUserAccount[position].birthDate
        Glide.with(holder.itemView).load(listUserAccount[position].image).into(holder.binding.imgUser)

        }

    override fun getItemCount(): Int {
        return listUserAccount.size
    }
}