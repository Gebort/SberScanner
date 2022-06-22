package com.example.sberqrscanner.presentation.scanner.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sberqrscanner.R
import com.example.sberqrscanner.domain.model.Division
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class DivisionCheckListAdapter: ListAdapter<DivisionItem, DivisionCheckListAdapter.ItemViewholder>(DiffCallback())  {

    private val adapterScope = CoroutineScope(Dispatchers.Default)
    private var rawList = listOf<DivisionItem>()
    private var filter: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewholder {
        return ItemViewholder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.division_check_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: DivisionCheckListAdapter.ItemViewholder, position: Int) {
        holder.bind(getItem(position))
    }

    class ItemViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: DivisionItem) = with(itemView) {
            val textDivision: TextView = this.findViewById(R.id.text_division_name)
            textDivision.text = item.division.name
            val imageView: ImageView = this.findViewById(R.id.image_check)
            imageView.setImageResource(
                if (item.checked) R.drawable.ic_check else R.drawable.ic_absent
            )
        }
    }

    fun changeList(entries: List<DivisionItem>){
        rawList = entries
        //filter = ""
        submitList(rawList)
    }
}

class DiffCallback : DiffUtil.ItemCallback<DivisionItem>() {
    override fun areItemsTheSame(oldItem: DivisionItem, newItem: DivisionItem): Boolean {
        return oldItem.division.name == newItem.division.name
    }

    override fun areContentsTheSame(oldItem: DivisionItem, newItem: DivisionItem): Boolean {
        return oldItem == newItem
    }
}