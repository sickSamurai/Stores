package com.BlackPanthers.stores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.BlackPanthers.stores.StoreAdapter.ViewHolder
import com.BlackPanthers.stores.databinding.ItemStoreBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class StoreAdapter(private var stores: MutableList<Store>, private var controller: ItemStoreController) : Adapter<ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding = ItemStoreBinding.bind(itemView)
    }

    private lateinit var context: Context

    fun setStores(stores: MutableList<Store>) {
        this.stores = stores
        notifyDataSetChanged()
    }

    fun addStore(store: Store) {
        stores.add(store)
        notifyItemInserted(stores.indexOf(store))
    }


    fun editStore(store: Store) {
        with(stores.indexOf(store)) {
            if (this != -1) {
                stores[this] = store
                notifyItemChanged(this)
            }
        }
    }

    fun deleteStore(store: Store) {
        with(stores.indexOf(store)) {
            if (this != 1) {
                stores.removeAt(this)
                notifyItemRemoved(this)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        context = viewGroup.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_store, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val store = stores[position]
        with(holder.binding) {
            root.setOnClickListener { controller.onItemStoreClick(store.id) }
            root.setOnLongClickListener { controller.onItemStoreLongClick(store) }
            checkboxIsFavorite.setOnClickListener { controller.onCheckBoxFavoriteClick(store) }
            checkboxIsFavorite.isChecked = store.isFavorite
            storeName.text = store.name
            Glide.with(context)
                .load(store.urlPhoto)
                .error(R.drawable.ic_broken_image)
                .centerInside()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgStore)
        }
    }

    override fun getItemCount(): Int = stores.size

}