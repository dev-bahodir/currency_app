package dev.bahodir.networkentrancelessonmedium.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import dev.bahodir.networkentrancelessonmedium.databinding.RvItemBinding
import dev.bahodir.networkentrancelessonmedium.user.User

class RV(var listener: OnTouchListener) : ListAdapter<User, RV.VH>(DU()) {

    inner class VH(var binding: RvItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(user: User, position: Int) {
            Picasso.get().load(user.image).into(binding.imageUrl)
            binding.moneyName.text = user.CcyNm_EN
            binding.moneyCourse.text = "1 ${user.Ccy} = ${user.Rate} UZS"

            itemView.setOnClickListener {
                listener.itemClick(user = user, position = position, view = it)
            }

            binding.like.setOnClickListener {
                listener.likeClick(user = user, position = position, view = it)
            }
        }
    }

    class DU : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }

    interface OnTouchListener {
        fun itemClick(user: User, position: Int, view: View)

        fun likeClick(user: User, position: Int, view: View)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(RvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBind(getItem(position), position)
    }
}