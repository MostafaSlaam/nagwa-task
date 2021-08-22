package com.example.nagwatask.adapter

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.nagwatask.R
import com.example.nagwatask.databinding.ItemRecyclerBinding
import com.example.nagwatask.model.ItemModel
import kotlinx.android.synthetic.main.item_recycler.view.*
import java.io.File


class RecyclerItemsAdapter(
    var items: ArrayList<ItemModel>, var listener: OnRecyclerItemClickListener
) : RecyclerView.Adapter<RecyclerItemsAdapter.ViewHolder>() {

    lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_recycler,
            parent,
            false
        ) as ItemRecyclerBinding
        context = parent.context
        return RecyclerItemsAdapter.ViewHolder(binding)
    }

    override fun getItemCount() = items.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = items[holder.adapterPosition]
        holder.binding.model = item
        holder.binding.btnDwonload.isVisible = !fileExit(item) && !item.isLoading
        holder.binding.layoutLoader.isVisible = item.isLoading
        holder.binding.root.setOnClickListener {
            listener.onRecyclerItemClickListener(item)
        }
        holder.binding.btnDwonload.setOnClickListener {
            listener.onRecyclerItemClickListener(item)
        }
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)
        if (payloads.firstOrNull() != null) {
            with(holder.itemView) {
                var bundle = payloads.first() as Bundle
                val progress = bundle.getInt("progress")
                val type = bundle.getString("type")
                btn_dwonload.isVisible = false
                layout_loader.isVisible = progress < 99
                if (type!!.compareTo("%")==0)
                tv_percentage.text = "$progress $type"

            }
        }
    }

    fun setDownloading(dummy: ItemModel, isDownloading: Boolean) {
        getDummy(dummy)?.isLoading = isDownloading
        notifyItemChanged(items.indexOf(dummy))
    }

    fun setProgress(dummy: ItemModel, progress: Int, type: String) {
        getDummy(dummy)?.progress = progress
        notifyItemChanged(items.indexOf(dummy), Bundle().apply {
            putInt("progress", progress)
            putString("type", type)
        })
    }

    private fun getDummy(dummy: ItemModel) = items.find { dummy.id == it.id }

    fun setList(list: ArrayList<ItemModel>) {
        this.items = list
        notifyDataSetChanged()
    }

    class ViewHolder(var binding: ItemRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }


    fun fileExit(item: ItemModel): Boolean {
        var extention: String = when (item.type) {
            "VIDEO" -> {
                ".mp4"
            }
            "PDF" -> {
                ".pdf"
            }
            else -> {
                ""
            }
        }
        return File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            item.name + extention
        ).exists()
    }

}