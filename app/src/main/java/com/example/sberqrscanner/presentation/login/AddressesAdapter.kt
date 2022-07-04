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
    private val onClickAddress: (Address) -> Unit,
    private val onClickNew: () -> Unit
): ListAdapter<AddressItem, AddressesAdapter.AdapterViewHolder>(DiffCallback())  {

    private var rawList = listOf<AddressItem>()
    private var selectedIndex: Int = -1

    inner class AdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private fun bindRealAddress(
            item: Address,
            onClick: (Address) -> Unit
        ) = with(itemView) {
                val textAddress: TextView = this.findViewById(R.id.text_address_name)
                textAddress.text = item.name
                val imageCheck: ImageView = this.findViewById(R.id.image_check)
                imageCheck.isVisible = layoutPosition == selectedIndex
                setOnClickListener {
                    onClick(item)
                    run {
                        if (selectedIndex != layoutPosition) {
                            notifyItemChanged(selectedIndex)
                        }
                    }
                    selectedIndex = layoutPosition
                    notifyItemChanged(selectedIndex)

                }
            }
        private fun bindNewAddress(onClick: () -> Unit) = with(itemView) {
                setOnClickListener {
                    onClick()
                }
            }
        fun bind(
            item: AddressItem,
            onClickAddress: (Address) -> Unit,
            onClickNew: () -> Unit)
        {
            when (item) {
                is AddressItem.RealAddress -> bindRealAddress(item.address, onClickAddress)
                is AddressItem.NewAddress -> bindNewAddress(onClickNew)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is AddressItem.RealAddress -> TYPE_ADDRESS
            is AddressItem.NewAddress -> TYPE_NEW
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterViewHolder {

        val layout = when (viewType) {
            TYPE_ADDRESS -> R.layout.address_item
            TYPE_NEW -> R.layout.new_address_item
            else -> throw IllegalArgumentException("Invalid address item type")
        }

        val view = LayoutInflater
            .from(parent.context)
            .inflate(layout, parent, false)

        return AdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        holder.bind(getItem(position), onClickAddress, onClickNew)
    }

    fun changeList(entries: List<Address>, canAdd: Boolean = true){
        rawList = entries
            .map { address ->
            AddressItem.RealAddress(address)
            }
        if (canAdd){
            rawList = rawList.plus(AddressItem.NewAddress)
        }

        submitList(rawList)
    }

    fun removeSelected() {
        run {
            notifyItemChanged(selectedIndex)
        }
        selectedIndex = -1

    }

    companion object {
        private const val TYPE_ADDRESS = 0
        private const val TYPE_NEW = 1
    }
}

class DiffCallback : DiffUtil.ItemCallback<AddressItem>() {
    override fun areItemsTheSame(oldItem: AddressItem, newItem: AddressItem): Boolean {
        return if (oldItem is AddressItem.RealAddress && newItem is AddressItem.RealAddress)
            oldItem.address.id == newItem.address.id
        else
            oldItem is AddressItem.NewAddress && newItem is AddressItem.NewAddress
    }

    override fun areContentsTheSame(oldItem: AddressItem, newItem: AddressItem): Boolean {
        return oldItem == newItem
    }
}