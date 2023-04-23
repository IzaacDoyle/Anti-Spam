package com.ab.anti_spam.ui.callblacklist

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.ab.anti_spam.R
import com.ab.anti_spam.databinding.CallblacklistCountryblockDialogBinding
import com.ab.anti_spam.databinding.CallblacklistOptionsDialogBinding
import com.ab.anti_spam.main.Main
import com.ab.anti_spam.models.CallBlacklistModel
import com.ab.anti_spam.ui.auth.LoggedInViewModel

class CountryBlockDialog: DialogFragment() {

    private var _fragBinding: CallblacklistCountryblockDialogBinding? = null
    private val fragBinding get() = _fragBinding!!
    private val blacklistViewModel: CallblacklistViewModel by activityViewModels()
    lateinit var app: Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as Main
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _fragBinding = CallblacklistCountryblockDialogBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val adapter = ArrayAdapter(requireContext(),R.layout.list_countries,resources.getStringArray(R.array.countries))
         fragBinding.countrySelect.setAdapter(adapter)
        fragBinding.BlockByCountry.isEnabled = true

        fragBinding.BlockByCountry.setOnClickListener{
            addBlock()
        }
        darkTheme()
        return root
    }

    private fun addBlock(){
        if(!fragBinding.textInputLayout.editText?.text.toString().isEmpty()) {
            val data = fragBinding.textInputLayout.editText?.text.toString()
            val model = CallBlacklistModel()
            model.by_country = data
            blacklistViewModel.addBlacklist(model, app)
            blacklistViewModel.changeTab(0)
            dismiss()
            fragBinding.BlockByCountry.isEnabled = false
        }
    }

    fun darkTheme(){
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            fragBinding.root.setBackgroundResource(R.drawable.dialog_background_dark)
            fragBinding.header.setTextColor(Color.WHITE)
        }else{
            fragBinding.root.setBackgroundResource(R.drawable.dialog_background)
        }
    }
}