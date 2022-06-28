package com.example.sberqrscanner.presentation.division_edit

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sberqrscanner.R
import com.example.sberqrscanner.presentation.division_edit.adapter.CodeImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class CodeSliderAdapter: ListAdapter<CodeImage, CodeSliderAdapter.ItemViewholder>(DiffCallback())  {

    private var items = listOf<CodeImage?>()
    private var back: Bitmap? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewholder {
        return ItemViewholder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.code_slider_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CodeSliderAdapter.ItemViewholder, position: Int) {
        holder.bind(getItem(position), back)
    }

    class ItemViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: CodeImage, back: Bitmap?) = with(itemView) {
            val backImage: ImageView = this.findViewById(R.id.image_code_back)
            backImage.setImageBitmap(back)
            val codeImage: ImageView = this.findViewById(R.id.image_code)
            codeImage.setImageBitmap(item.bitmap)
        }
    }

    fun changeList(entries: List<CodeImage>){
        items = entries
        submitList(items)
    }

    fun setBackground(bitmap: Bitmap){
        back = bitmap
    }
}

class DiffCallback : DiffUtil.ItemCallback<CodeImage?>() {
    override fun areItemsTheSame(oldItem: CodeImage, newItem: CodeImage): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: CodeImage, newItem: CodeImage): Boolean {
        return oldItem == newItem
    }
}