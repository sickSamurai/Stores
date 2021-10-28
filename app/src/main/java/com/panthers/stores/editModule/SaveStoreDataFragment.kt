package com.panthers.stores.editModule

import android.os.Bundle
import android.view.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.panthers.stores.R
import com.panthers.stores.database.entities.Store
import com.panthers.stores.databinding.FragmentSaveStoreDataBinding
import com.panthers.stores.editModule.viewModel.SaveStoreDataViewModel
import com.panthers.stores.mainModule.MainActivity
import com.panthers.stores.utils.KeyboardHider
import kotlinx.coroutines.*

class SaveStoreDataFragment : Fragment(), CoroutineScope by MainScope() {

    private var mainActivity: MainActivity? = null
    private lateinit var binding: FragmentSaveStoreDataBinding
    private var isEditMode: Boolean = false
    private lateinit var saveStoreDataViewModel: SaveStoreDataViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSaveStoreDataBinding.inflate(inflater, container, false)
        mainActivity = activity as? MainActivity
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setPhotoAutoCharge()
        setInputAutoValidation()
        setupActionBar()
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
        KeyboardHider.hideKeyboard(this)
        super.onDestroyView()
    }

    override fun onDestroy() {
        saveStoreDataViewModel.showFAB()
        resetActionBar()
        super.onDestroy()
    }

    private fun setupViewModel() {
        saveStoreDataViewModel = ViewModelProvider(requireActivity()).get(SaveStoreDataViewModel::class.java)
        saveStoreDataViewModel.getSelectedStoreLiveData().observe(viewLifecycleOwner) {
            if (it != null) {
                isEditMode = true
                chargeDataOfStoreToEdit(it)
            }
        }
    }

    private fun setupActionBar() {
        mainActivity?.supportActionBar?.title =
            if (isEditMode) getString(R.string.edit_store_fragment_title) else getString(R.string.create_store_fragment_title)
        mainActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
    }

    private fun resetActionBar() {
        mainActivity?.supportActionBar?.title = getString(R.string.app_name)
        mainActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        setHasOptionsMenu(false)
    }

    private fun setPhotoAutoCharge() {
        with(binding) {
            inputUrlPhoto.addTextChangedListener {
                Glide.with(this@SaveStoreDataFragment)
                    .load(inputUrlPhoto.text.toString())
                    .error(R.drawable.ic_broken_image)
                    .centerInside()
                    .into(imagePhoto)
            }
        }
    }

    private fun isInputValid(inputLayout: TextInputLayout): Boolean {
        var isValid = true
        with(inputLayout.editText?.text?.trim().toString()) {
            if (isBlank()) {
                inputLayout.error = getString(R.string.required_input)
                inputLayout.requestFocus()
                isValid = false
            } else inputLayout.error = null
        }
        return isValid
    }

    private fun setInputAutoValidation() {
        with(binding) {
            inputName.addTextChangedListener { isInputValid(layoutInputName) }
            inputPhone.addTextChangedListener { isInputValid(layoutInputPhone) }
        }
    }

    private fun isStoreDataValid(): Boolean {
        with(binding) {
            val isValid = isInputValid(layoutInputName) && isInputValid(layoutInputPhone)
            if (!isValid) Snackbar.make(root, R.string.invalid_field_msg, Snackbar.LENGTH_SHORT).show()
            return isValid
        }
    }

    private fun chargeDataOfStoreToEdit(store: Store) {
        binding.apply {
            inputName.setText(store.name)
            inputPhone.setText(store.phone)
            inputWebsite.setText(store.website)
            inputUrlPhoto.setText(store.urlPhoto)
        }
    }

    private fun getStore(): Store {
        var store = saveStoreDataViewModel.getSelectedStoreLiveData().value
        if (store == null) store = Store()
        return store.apply {
            name = binding.inputName.text.toString()
            phone = binding.inputPhone.text.toString()
            website = binding.inputWebsite.text.toString()
            urlPhoto = binding.inputUrlPhoto.text.toString()
        }
    }

    private fun saveNewStore() {
        val newStore = getStore()
        saveStoreDataViewModel.saveNewStore(newStore)
        Snackbar.make(binding.root, R.string.successful_store_creation_msg, Snackbar.LENGTH_SHORT).show()
    }

    private fun editStore() {
        val storeToEdit = getStore()
        saveStoreDataViewModel.editStore(storeToEdit)
        Snackbar.make(binding.root, R.string.successful_store_update_msg, Snackbar.LENGTH_SHORT).show()
    }

    private fun onSaveStoreAction() {
        launch {
            KeyboardHider.hideKeyboard(this@SaveStoreDataFragment)
            if (isStoreDataValid()) if (isEditMode) editStore() else saveNewStore()
            mainActivity?.onBackPressed()
        }
    }

}
