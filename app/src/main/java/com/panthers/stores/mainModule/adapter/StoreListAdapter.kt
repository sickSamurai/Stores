package com.panthers.stores.mainModule.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.panthers.stores.R
import com.panthers.stores.database.entities.Store
import com.panthers.stores.databinding.ItemStoreBinding

class StoreListAdapter(private var controller: ItemStoreController) : ListAdapter<Store, RecyclerView.ViewHolder>(StoreDiffCallback()) {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding = ItemStoreBinding.bind(itemView)

        fun setupItemController(store: Store) {
            with(binding) {
                root.setOnClickListener { controller.onItemStoreClick(store) }
                root.setOnLongClickListener { controller.onItemStoreLongClick(store) }
                checkboxIsFavorite.setOnClickListener { controller.onCheckBoxFavoriteClick(store) }
            }
        }

        fun updateUI(store: Store) {
            with(binding) {
                storeName.text = store.name
                checkboxIsFavorite.isChecked = store.isFavorite
                Glide.with(context)
                    .load(store.urlPhoto)
                    .error(R.drawable.ic_broken_image)
                    .centerInside()
                    .into(imgStore)
            }
        }
    }

    class StoreDiffCallback : DiffUtil.ItemCallback<Store>() {
        override fun areItemsTheSame(oldItem: Store, newItem: Store): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Store, newItem: Store): Boolean {
            return oldItem == newItem
        }
    }

    private lateinit var context: Context

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        context = viewGroup.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_store, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val store = getItem(position)
        with(holder as StoreListAdapter.ViewHolder) {
            setupItemController(store)
            updateUI(store)
        }
    }

}