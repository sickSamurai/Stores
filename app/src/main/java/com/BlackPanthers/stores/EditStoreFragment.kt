package com.BlackPanthers.stores

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.BlackPanthers.stores.databinding.FragmentEditStoreBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.*

class EditStoreFragment : Fragment(), CoroutineScope by MainScope() {

    private var mainActivity: MainActivity? = null
    private lateinit var binding: FragmentEditStoreBinding
    private var isEditMode: Boolean = false
    private lateinit var store: Store

    private fun hideKeyboard() {
        with(mainActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager) {
            hideSoftInputFromWindow(view!!.windowToken, 0)
        }
    }

    private fun setPhotoAutoCharge() {
        with(binding) {
            inputUrlPhoto.addTextChangedListener {
                Glide.with(this@EditStoreFragment)
                        .load(inputUrlPhoto.text.toString())
                        .error(R.drawable.ic_broken_image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerInside()
                        .into(imagePhoto)
            }
        }
    }

    private fun setInputAutoValidation(){
        with(binding){
            inputName.addTextChangedListener { isInputValid(layoutInputName) }
            inputPhone.addTextChangedListener { isInputValid(layoutInputPhone) }
        }
    }

    private fun isInputValid(inputLayout: TextInputLayout): Boolean {
        var isValid = true
        with(inputLayout.editText?.text?.trim().toString()) {
            if (isBlank() or isEmpty()) {
                inputLayout.error = getString(R.string.required_input)
                inputLayout.requestFocus()
                isValid = false
            } else inputLayout.error = null
        }
        return isValid
    }

    private fun isStoreDataValid(): Boolean {
        with(binding) {
            val isValid = isInputValid(layoutInputName) && isInputValid(layoutInputPhone)
            if (!isValid) Snackbar.make(root, R.string.invalid_field_msg,Snackbar.LENGTH_SHORT).show()
            return isValid
        }
    }

    private suspend fun chargeStoreData(id: Long){
        store = withContext(Dispatchers.IO) { StoreApp.db.storeDAO().getStoreById(id) }
        with(binding) {
            inputName.setText(store.name)
            inputPhone.setText(store.phone)
            inputWebsite.setText(store.website)
            inputUrlPhoto.setText(store.urlPhoto)
        }
    }

    private fun setStoreData() {
        store.apply {
            name = binding.inputName.text.toString()
            phone = binding.inputPhone.text.toString()
            website = binding.inputWebsite.text.toString()
            urlPhoto = binding.inputUrlPhoto.text.toString()
        }
    }

    private suspend fun addStore() {
        store.id = withContext(Dispatchers.IO) { StoreApp.db.storeDAO().addStore(store) }
        mainActivity?.addStoreItem(store)
        Snackbar.make(binding.root, R.string.successful_store_creation_msg, Snackbar.LENGTH_SHORT).show()
        mainActivity?.onBackPressed()
    }

    private suspend fun editStore() {
        withContext(Dispatchers.IO) { StoreApp.db.storeDAO().editStore(store) }
        mainActivity?.editStoreItem(store)
        Snackbar.make(binding.root, R.string.successful_store_update_msg, Snackbar.LENGTH_SHORT).show()
        mainActivity?.onBackPressed()
    }

    private fun onSaveStoreAction() {
        launch {
            hideKeyboard()
            setStoreData()
            if (isStoreDataValid()) if (isEditMode) editStore() else addStore()
        }
    }

    private fun setupActionBar() {
        mainActivity?.supportActionBar?.title = if (isEditMode) getString(R.string.edit_store_fragment_title) else getString(R.string.create_store_fragment_title)
        mainActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEditStoreBinding.inflate(inflater, container, false)
        mainActivity = activity as? MainActivity
        val id = arguments?.getLong(getString(R.string.edit_store_fragment_args_id), 0)
        launch {
            isEditMode = id != 0L && id != null
            if (isEditMode) chargeStoreData(id!!)
            else store = Store(name = "")
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBar()
        setPhotoAutoCharge()
        setInputAutoValidation()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save_store, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> mainActivity?.onBackPressed()
            R.id.action_save_store -> onSaveStoreAction()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        hideKeyboard()
        super.onDestroyView()
    }

    override fun onDestroy() {
        mainActivity?.supportActionBar?.title = getString(R.string.app_name)
        mainActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        setHasOptionsMenu(false)
        mainActivity?.showBtnAdd()
        super.onDestroy()
    }

}