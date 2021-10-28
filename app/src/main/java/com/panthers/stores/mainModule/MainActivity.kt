package com.panthers.stores.mainModule

import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.panthers.stores.*
import com.panthers.stores.database.entities.Store
import com.panthers.stores.databinding.ActivityMainBinding
import com.panthers.stores.editModule.SaveStoreDataFragment
import com.panthers.stores.editModule.viewModel.SaveStoreDataViewModel
import com.panthers.stores.mainModule.adapter.ItemStoreController
import com.panthers.stores.mainModule.adapter.StoreListAdapter
import com.panthers.stores.mainModule.viewModel.MainViewModel
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var storeListAdapter: StoreListAdapter
    private lateinit var mainViewModel: MainViewModel
    private lateinit var saveStoreDataViewModel: SaveStoreDataViewModel

    private val itemStoreController = object : ItemStoreController {
        override fun onItemStoreClick(store: Store) {
            launchEditStoreFragment(store.id)
        }

        override fun onItemStoreLongClick(store: Store): Boolean {
            disposeStoreOptions(store)
            return true
        }

        override fun onCheckBoxFavoriteClick(store: Store) {
            toggleStoreFavorability(store)
        }
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        saveStoreDataViewModel = ViewModelProvider(this).get(SaveStoreDataViewModel::class.java)
        saveStoreDataViewModel.getFABVisibilityLiveData().observe(this) { isVisible -> if (isVisible) showFAB() else hideFAB() }
        mainViewModel.getVisibiltyOfProgressBarLiveData().observe(this) { binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE }
        mainViewModel.getStoresLiveData().observe(this) { stores ->
            mainViewModel.changeProgressBarVisibility(true)
            storeListAdapter.submitList(stores)
            mainViewModel.changeProgressBarVisibility(false)
        }
        //editStoreViewModel.getStoreCreatedLiveData().observe(this) { createdStore -> }
        //editStoreViewModel.getStoreEditedLiveData().observe(this) { editedStore -> }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            setHasFixedSize(true)
            adapter = this@MainActivity.storeListAdapter
            layoutManager = GridLayoutManager(this@MainActivity, resources.getInteger(R.integer.grid_columns_number))
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    super.getItemOffsets(outRect, view, parent, state)
                    with(resources.getDimension(R.dimen.common_spacing_min).toInt()) { outRect.set(this, this, this, this) }
                }
            })
        }
    }

    private fun disposeStoreOptions(store: Store) {
        val options = resources.getStringArray(R.array.store_lc_options)
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.store_item_lc_options)
            .setItems(options) { dialogInterface, i ->
                when (i) {
                    0 -> dial(store.phone)
                    1 -> tryGoToWebSite(store.website)
                    2 -> confirmDelete(store)
                }
            }.show()
    }

    private fun confirmDelete(store: Store) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.delete_store_advise)
            .setPositiveButton(R.string.confirm_dialog_to_delete_store_afirmative_option) { _, _ -> deleteStore(store) }
            .setNegativeButton(R.string.confirm_dialog_to_delete_store_negative_option, null)
            .show()
    }

    fun showFAB() {
        binding.floatingActionButton.show()
    }

    fun hideFAB() {
        binding.floatingActionButton.hide()
    }

    fun deleteStore(store: Store) {
        mainViewModel.deleteStore(store)
        Toast.makeText(applicationContext, "store#$store.id deleted", Toast.LENGTH_SHORT)
    }

    private fun toggleStoreFavorability(store: Store) {
        mainViewModel.toggleStoreFavorability(store)
    }

    private fun startIntent(intent: Intent) {
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

    private fun launchEditStoreFragment(id: Long?) {
        if (id != null) saveStoreDataViewModel.retrieveSelectedStoreFromDB(id)
        else saveStoreDataViewModel.setSelectedStoreToNull()
        val fragment = SaveStoreDataFragment()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.container_activity_main, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    fun onFABClick() {
        launchEditStoreFragment(null)
        saveStoreDataViewModel.hideFAB()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        storeListAdapter = StoreListAdapter(itemStoreController)
        binding.floatingActionButton.setOnClickListener { onFABClick() }
        setupViewModel()
        setupRecyclerView()
        setContentView(binding.root)
    }
}