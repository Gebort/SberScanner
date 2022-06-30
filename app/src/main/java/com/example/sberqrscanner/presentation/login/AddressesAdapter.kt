package com.example.sberqrscanner.presentation.login

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sberqrscanner.R
import com.example.sberqrscanner.domain.login.Address

class AddressesAdapter(
    private val onClick: (Address) -> Unit
): ListAdapter<Address, AddressesAdapter.ItemViewholder>(DiffCallback())  {

    private var rawList = listOf<Address>()
    private var selectedIndex: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewholder {
        return ItemViewholder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.address_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewholder, position: Int) {
        holder.bind(getItem(position), onClick)
    }

    inner class ItemViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Address, onClick: (Address) -> Unit) = with(itemView) {
            val textAddress: TextView = this.findViewById(R.id.text_address_name)
            textAddress.text = item.name
            val imageCheck: ImageView = this.findViewById(R.id.image_check)
            imageCheck.isVisible = layoutPosition == selectedIndex
            setOnClickListener {
                onClick(item)
                run {
                    if (selectedIndex != layoutPosition){
                        notifyItemChanged(selectedIndex)
                    }
                }
                selectedIndex = layoutPosition
                notifyItemChanged(selectedIndex)

            }
        }
    }

    fun changeList(entries: List<Address>){
        rawList = entries
        submitList(rawList)
    }

    fun removeSelected() {
        run {
            notifyItemChanged(selectedIndex)
        }
        selectedIndex = -1

    }
}

class DiffCallback : DiffUtil.ItemCallback<Address>() {
    override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
        return oldItem.name == newItem.name
    }
}