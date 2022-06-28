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

class DivisionCheckListAdapter(
    private val onClick: (Division) -> Unit
): ListAdapter<Division, DivisionCheckListAdapter.ItemViewholder>(DiffCallback())  {

    private val adapterScope = CoroutineScope(Dispatchers.Default)
    private var rawList = listOf<Division>()
    private var filter: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewholder {
        return ItemViewholder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.division_check_item, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: DivisionCheckListAdapter.ItemViewholder, position: Int) {
        holder.bind(getItem(position), onClick)
    }

    class ItemViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(division: Division, onClick: (Division) -> Unit) = with(itemView) {
            val textDivision: TextView = this.findViewById(R.id.text_division_name)
            textDivision.text = division.name
            val imageView: ImageView = this.findViewById(R.id.image_check)
            imageView.setImageResource(
                if (division.checked) R.drawable.ic_check else R.drawable.ic_absent
            )
            setOnClickListener {
                onClick(division)
            }
        }
    }

    fun changeList(entries: List<Division>){
        rawList = entries
        //filter = ""
        submitList(rawList)
    }
}

class DiffCallback : DiffUtil.ItemCallback<Division>() {
    override fun areItemsTheSame(oldItem: Division, newItem: Division): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Division, newItem: Division): Boolean {
        return oldItem.checked == newItem.checked
    }
}