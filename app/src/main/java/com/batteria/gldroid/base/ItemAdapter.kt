package com.batteria.gldroid.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.batteria.gldroid.R

/**
 * @author: yaobeihaoyu
 * @version: 1.0
 * @since: 2021/7/29
 * @description:
 */
class ItemAdapter : RecyclerView.Adapter<ItemAdapter.ConsolePluginViewHandler>() {
    private val pluginItems = mutableListOf<Item>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsolePluginViewHandler =
        ConsolePluginViewHandler(LayoutInflater.from(parent.context).inflate(R.layout.view_item_tools, parent, false))

    override fun getItemCount(): Int = pluginItems.size

    override fun onBindViewHolder(holder: ConsolePluginViewHandler, position: Int) {
        val item = pluginItems[position]
        holder.name.text = item.name
        holder.image.setImageResource(item.resId)
        holder.itemView.setOnClickListener{
            item.clickAction.invoke()
        }
    }

    fun setItems(items: List<Item>) { pluginItems.addAll(items) }

    fun addItem(item: Item) { pluginItems.add(item) }

    inner class ConsolePluginViewHandler(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.item_image_view)
        val name: TextView = itemView.findViewById(R.id.item_text_view)
    }
}

class Item(val name: String, @DrawableRes val resId: Int, val clickAction: () -> Unit)