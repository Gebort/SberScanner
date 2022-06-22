package com.example.sberqrscanner.presentation.division_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sberqrscanner.R
import com.example.sberqrscanner.domain.model.Division
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class DivisionsListAdapter(
    private val deleteDivision: (Division) -> Unit
): ListAdapter<Division, DivisionsListAdapter.ItemViewholder>(DiffCallback())  {

    private val adapterScope = CoroutineScope(Dispatchers.Default)
    private var rawList = listOf<Division>()
    private var editing: Boolean = false
    private var filter: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewholder {
        return ItemViewholder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.division_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: DivisionsListAdapter.ItemViewholder, position: Int) {
        holder.bind(getItem(position), editing, deleteDivision)
    }

    class ItemViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Division, editing: Boolean, delete: (Division) -> Unit) = with(itemView) {
            val textDivision: TextView = this.findViewById(R.id.text_division_name)
            textDivision.text = item.name
            val deleteButton: ImageButton = this.findViewById(R.id.button_delete)
            deleteButton.isVisible = editing
            deleteButton.setOnClickListener{delete(item)}
        }
    }

    fun changeList(entries: List<Division>){
        rawList = entries
        //filter = ""
        submitList(rawList)
    }

    fun setEditing(editing: Boolean){
        this.editing = editing
        //this.filter = filter
        notifyDataSetChanged()
    }
}

class DiffCallback : DiffUtil.ItemCallback<Division>() {
    override fun areItemsTheSame(oldItem: Division, newItem: Division): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Division, newItem: Division): Boolean {
        return oldItem == newItem
    }
}