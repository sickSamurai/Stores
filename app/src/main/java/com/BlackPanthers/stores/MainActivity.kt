package com.BlackPanthers.stores


import android.content.DialogInterface
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.BlackPanthers.stores.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var storeAdapter: StoreAdapter
    private val itemStoreController = object : ItemStoreController {
        override fun onItemStoreClick(id: Long) {
            val args = Bundle()
            args.putLong(getString(R.string.edit_store_fragment_args_id), id)
            launchEditStoreFragment(args)
        }

        override fun onItemStoreLongClick(store: Store): Boolean {
            disposeStoreOptions(store)
            return true
        }

        override fun onCheckBoxFavoriteClick(store: Store) {
            toggleStoreFavorability(store)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            setHasFixedSize(true)
            adapter = this@MainActivity.storeAdapter
            layoutManager = GridLayoutManager(this@MainActivity, resources.getInteger(R.integer.grid_columns))
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    super.getItemOffsets(outRect, view, parent, state)
                    with(resources.getDimension(R.dimen.common_spacing_min).toInt()) { outRect.set(this, this, this, this) }
                }
            })
        }
    }

    suspend fun chargeStoresFromDB() {
        val stores = withContext(Dispatchers.IO) { StoreApp.db.storeDAO().getAll() }
        storeAdapter.setStores(stores)
    }

    private fun disposeStoreOptions(store: Store){
        val options = resources.getStringArray(R.array.store_lc_options)
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.store_item_lc_options)
            .setItems(options, DialogInterface.OnClickListener { dialogInterface, i ->
                when (i) {
                    0 -> dial(store.phone)
                    1 -> tryGoToWebSite(store.website)
                    2 -> if (confirmDelete()) deleteStore(store)
                }
            }).show()
    }

    private fun confirmDelete(): Boolean {
        var delete = false
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.delete_store_advise)
            .setPositiveButton(R.string.confirm_dialog_store_afirmative) { dialogInterface, i -> delete = true }
            .setNegativeButton(R.string.confirm_dialog_negative) { dialogInterface, i -> delete = false }
            .show()
        return delete
    }

    fun showBtnAdd() {
        binding.btnAdd.show()
    }

    fun hideBtnAdd() {
        binding.btnAdd.hide()
    }

    fun addStoreItem(store: Store) {
        storeAdapter.addStore(store)
    }

    fun editStoreItem(store: Store) {
        storeAdapter.editStore(store)
    }

    private fun toggleStoreFavorability(store: Store) {
        store.apply { isFavorite = !isFavorite }
        launch(Dispatchers.IO) { StoreApp.db.storeDAO().editStore(store) }
        editStoreItem(store)
    }

    fun deleteStore(store: Store) {
        launch(Dispatchers.IO) { StoreApp.db.storeDAO().deleteStore(store) }
        storeAdapter.deleteStore(store)
    }

    private fun startIntent(intent: Intent){
        if (intent.resolveActivity(packageManager) != null) startActivity(intent)
        else Snackbar.make(binding.root, R.string.no_compatible_app, Snackbar.LENGTH_SHORT).show()
    }

    private fun dial(phone: String) {
        val dialIntent = Intent().apply { action = Intent.ACTION_DIAL; data = Uri.parse("tel:$phone") }
        startIntent(dialIntent)
    }

    private fun tryGoToWebSite(urlWebsite: String) {
        if (urlWebsite.isEmpty()) {
            Snackbar.make(binding.root, R.string.no_web_msg, Snackbar.LENGTH_SHORT).show()
        } else if (!Patterns.WEB_URL.matcher(urlWebsite).matches()) {
            Snackbar.make(binding.root, R.string.invalid_url, Snackbar.LENGTH_SHORT).show()
        } else {
            val websiteIntent = Intent().apply { action = Intent.ACTION_VIEW; data = Uri.parse(urlWebsite) }
            startIntent(websiteIntent)
        }
    }

    private fun launchEditStoreFragment(args: Bundle? = null) {
        val fragment = EditStoreFragment()
        if (args != null) fragment.arguments = args
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.container_activity_main, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    fun onBtnAddClick() {
        launchEditStoreFragment()
        hideBtnAdd()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        storeAdapter = StoreAdapter(mutableListOf(), itemStoreController)
        binding.btnAdd.setOnClickListener { onBtnAddClick() }
        setupRecyclerView()
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        launch { chargeStoresFromDB() }
    }
}